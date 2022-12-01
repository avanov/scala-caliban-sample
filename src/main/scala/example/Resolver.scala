package example

import scala.util.Try

import zio.{Task, ZIO}
import zio.query.ZQuery

import caliban.GraphQL.graphQL
import caliban.{CalibanError, GraphQL, GraphQLAspect, InputValue, RootResolver, Value}
import caliban.schema.{ArgBuilder, Schema}
import caliban.schema.Annotations.*
import caliban.federation.{EntityResolver, GQLKey, federated}


@GQLInputName("ItemKeyInput")
final case class ItemKeyInput
    (   id: String
    )

@GQLInputName("ItemFilter")
final case class ItemFilter
    (   itemKey: ItemKeyInput
    )

@GQLInputName("ItemsFilter")
final case class ItemsFilter
    (   itemKeys: List[ItemKeyInput]
    )


final case class ItemInput(filter: ItemFilter)
final case class ItemsInput(filter: ItemsFilter)

// There's still some inlining errors, see
// https://github.com/ghostdogpr/caliban/issues/1335#issuecomment-1088727957
implicit val itemFilterSchema:          Schema[Any, ItemInput]      = Schema.gen
implicit val itemFilterArgBuilder:      ArgBuilder[ItemInput]       = ArgBuilder.gen

implicit val itemsFilterSchema:         Schema[Any, ItemsInput]     = Schema.gen
implicit val itemsFilterArgBuilder:     ArgBuilder[ItemsInput]      = ArgBuilder.gen


@GQLKey("valA")
@GQLKey("valA valB")
final case class Item
    (   valA: String
    ,   valB: String
    ,   valC: String
    )

final case class Wrap(inner: List[Item])

final case class Query
    (   item:   ItemInput   => ZIO[Any, Throwable, Item]
    ,   items:  ItemsInput  => ZIO[Any, Throwable, Wrap]
    )


private val phonyItem = Item("A", "B", "C")


def getItem(args: ItemInput): ZIO[Any, Throwable, Item] =
    ZIO.succeed(phonyItem)


def getItems(args: ItemsInput): ZIO[Any, Throwable, Wrap] =
    ZIO.succeed(Wrap(List(phonyItem)))


private val queries = Query
    (   getItem
    ,   getItems
    )


// A helper method to be used for mapping errors of the resolver to the errors of federation
def federationFail(msg: String)(err: Throwable) =
    CalibanError.ExecutionError(
        msg = msg,
        path = List(),
        locationInfo = None,
        innerThrowable = Some(err),
        extensions = None
    )

// A helper method to be used for mapping results of the resolver to the results of federation
def federationSuccess[A](value: A): Option[A] = Some(value)


// The federated annotation wraps schema additions around the resolver so that the gateway will recognize our schema
private val withFederation: GraphQLAspect[Nothing, Any] = federated
    (   EntityResolver.from[ItemInput]
            (   (args: ItemInput) =>
                    ZQuery
                        .fromZIO    (getItem(args))
                        .mapError   (federationFail("Federated Item query failed"))
                        .map        (federationSuccess)
            )
    ,   EntityResolver.from[ItemsInput]
            (   (args: ItemsInput) =>
                    ZQuery
                        .fromZIO    (getItems(args))
                        .mapError   (federationFail("Federated Items query failed"))
                        .map        (federationSuccess)
            )
)

private val api: GraphQL[Any]   = graphQL[Any, Query, Unit, Unit](RootResolver(queries))
private val federatedApi        = api @@ withFederation

// TODO: BUG? replace `api.` with `federatedApi.` and the test suite will fail with:
//       caliban.CalibanError$ValidationError: ValidationError Error: Union _Entity contains the following non Object types: .
val resolver            = federatedApi.interpreter

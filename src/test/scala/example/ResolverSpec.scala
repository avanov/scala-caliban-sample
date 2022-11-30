package example

import zio.*
import zio.test.Assertion.*
import zio.test.*
import caliban.{CalibanError, GraphQLResponse, InputValue, Value}

import example.resolver


def runQ
    (   query:  String
    ,   vars:   Option[Map[String, InputValue]]
    ): ZIO[Any, CalibanError.ValidationError, GraphQLResponse[CalibanError]] =
        for
            r   <- resolver
            v   <- (r.executeRequest
                        (   caliban.GraphQLRequest
                                (   query       = Some(query)
                                ,   variables   = vars
                                )
                        )
                    )
        yield v


object ResolverSpec extends ZIOSpecDefault:
    def spec: Spec[Any, Any] = suite("ItemsResolverSpec")(
        test("Query single item type and basic attrs") {
            val query =
                """
                query {
                    item(filter: {itemKey: {id: "this is ID"}})
                        {
                            __typename
                            valA
                            valB
                            valC
                        }
                }
            """

            for     result <- runQ(query, None)
            yield
                (   assert  (result.errors)
                            (Assertion.equalTo(List()))

                &&  assert  (result.data.toString)
                            (Assertion.equalTo(
                                "{\"item\":{\"__typename\":\"Item\",\"valA\":\"A\",\"valB\":\"B\",\"valC\":\"C\"}}"
                            ))
                )

        }
    )

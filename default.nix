{
    jdkVersion      ? "jdk17_headless",
    projectName     ? "caliban-sample",
    environ         ? import ./nixpkgs
}:

let

customOverlays      = [];
env                 = environ { inherit customOverlays; };

pkgs                = env.pkgs;
java                = env.pkgs.${jdkVersion};


macOsSpecificTools  = [];
devEnv              = pkgs.mkShellNoCC {
     # Sets the build inputs, i.e. what will be available in our
     # local environment.
     nativeBuildInputs = with pkgs; [
        cacert
        gnumake

        java
        sbt

     ] ++ macOsSpecificTools;
     buildInputs = with pkgs; [
        openssl
     ];
     shellHook = ''
         export PROJECT_PLATFORM="${builtins.currentSystem}"
         export PROJECT_NAME="${projectName}"
         export LANG=en_GB.UTF-8

         # for no apparent reason, this prevents `ld: library not found for -liconv` errors on recent MacOS versions
         unset NIX_LDFLAGS

         # symbolic links to jdk and other java tooling for IntelliJ Idea project settings
         ln -s -nf "${java}/" $PWD/jdk

         export JAVA_HOME="${java.home}"
         export LOCAL_SBT="$PWD/.local/sbt"

         # See sbt options docs at https://www.scala-sbt.org/1.x/docs/Command-Line-Reference.html#Other+system+properties
         export SBT_OPTS="-Dsbt.boot.directory=$LOCAL_SBT/boot -Dsbt.global.base=$LOCAL_SBT/base -Dsbt.repository.config=$LOCAL_SBT/repos -Dsbt.ivy.home=$LOCAL_SBT/ivy"
     '';
};

in
{
    inherit devEnv;
}

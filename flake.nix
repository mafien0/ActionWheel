{
  description = "actionWheel devshell";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = {nixpkgs, ...}: let
    system = "x86_64-linux";
    pkgs = import nixpkgs {inherit system;};
    java = pkgs.temurin-bin-25;
  in {
    devShells.${system}.default = pkgs.mkShell {
      packages = with pkgs; [
        java
        gradle
      ];

      JAVA_HOME = "${java}";
    };
  };
}

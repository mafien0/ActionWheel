{pkgs, ...}: {
  languages.java = {
    enable = true;
    jdk.package = pkgs.jdk25;
    gradle.enable = true;
  };

  packages = [pkgs.gradle];
}

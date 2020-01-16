Installing and using the mCoq public release
--------------------------------------------

Note for ICSE Reviewers: We recently cleaned the code and improved our scripts for running the tool, so the steps shown in paper may differ from the ones below. Additionally, the directory structures may differ from the ones shown in the demo video.

0. Download and unpack the mCoq archive, and enter the directory:

```
$ wget https://cozy.ece.utexas.edu/mcoq/mcoq.tgz
$ tar xfz mcoq.tgz
$ cd mcoq
```

1. Make sure OPAM 2.0.5 is installed (https://opam.ocaml.org)

2. Make sure to use OCaml 4.07.1 via OPAM, for example:

```
$ opam switch create 4.07.1
$ opam switch 4.07.1
$ eval $(opam env)
```

3. Install Coq 8.10.2 and SerAPI 0.7.0 (pinning for convenience):

```
$ opam update
$ opam upgrade
$ opam pin add coq 8.10.2
$ opam pin add coq-serapi 8.10.0+0.7.0
```

4. For example, here is how to apply mCoq to StructTact revision b95f041cb83986fb0fe1f9689d7196e2f09a4839 (https://github.com/uwplse/StructTact):

```
$ ./mcoq.py --project StructTact --sha 82a85b7ec07e71fa6b30cfc05f6a7bfb09ef2510 --url https://github.com/uwplse/StructTact.git --buildcmd "./configure && make -j4" --qdir ".,StructTact"
```

This command should generate a HTML report in the reports directory.

# mCoq

mCoq is a tool for mutation analysis of verification projects that use the [Coq proof assistant](https://coq.inria.fr).

Note for ICSE-Demo reviewers: we recently cleaned up the code and improved our scripts for running the tool, so the steps shown in paper submission may differ from the ones below. Additionally, the directory structures may differ from the ones shown in the demo video.

## Requirements

- [OCaml 4.07.1](https://ocaml.org)
- [Coq 8.10.2](https://coq.inria.fr/download)
- [SerAPI 0.7.0](https://github.com/ejgallego/coq-serapi)
- [Python 3](https://www.python.org)
- [JDK 8](https://openjdk.java.net) (or later)
- [Gradle 6](https://gradle.org/install/)

## Installation and usage

We strongly recommend installing the required versions of OCaml, Coq,
and SerAPI via the [OPAM package manager](https://opam.ocaml.org/),
version 2.0.5 or later.

To set up the OPAM-based OCaml environment, use:
```
opam switch create 4.07.1
opam switch 4.07.1
eval $(opam env)
```

Then, install Coq and SerAPI, pinning them to avoid unintended upgrades:
```
opam update
opam pin add coq 8.10.2
opam pin add coq-serapi 8.10.0+0.7.0
```

Next, clone the mCoq repository and enter the directory:
```
git clone https://github.com/EngineeringSoftware/mcoq.git
cd mcoq
```

The entry point for using mCoq is the `mcoq.py` script. To see
the available options, run:
```
./mcoq.py --help
```

For example, to apply mCoq to [StructTact](https://github.com/uwplse/StructTact), revision [b95f041](https://github.com/uwplse/StructTact/commit/b95f041cb83986fb0fe1f9689d7196e2f09a4839), use:
```
./mcoq.py --project StructTact --sha b95f041 \
  --url https://github.com/uwplse/StructTact.git \
  --buildcmd "./configure && make -j4" --qdir ".,StructTact"
```
After running this command, look for a HTML report in the `reports` directory.

For large Coq projects, it is recommended to set the `--threads` option
to at least the number of CPU cores in the machine, since mutation analysis
may otherwise take a long time to complete.

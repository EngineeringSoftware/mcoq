# mCoq: Mutation Analysis for Coq

mCoq is a tool for mutation analysis of verification projects that use the
[Coq proof assistant](https://coq.inria.fr).

mCoq applies a set of mutation operators to Coq definitions, generating
modified versions, called mutants, of the project. If all proofs of a
mutant are successfully checked, a mutant is declared live; otherwise it
is declared killed. mCoq produces HTML reports pinpointing both live and
killed mutants in the Coq code, where live mutants may indicate
incomplete specifications. The [research paper][ase-paper] provides more
information on the technique and optimizations that mCoq implements.

Note to ICSE-Demo reviewers: we recently cleaned up the code and improved
our scripts for running the tool, so the steps shown in the paper submission
may differ from the ones below. Additionally, the directory structures
may differ from the ones shown in the demo video.

## Requirements

- [OCaml 4.07.1](https://ocaml.org)
- [Coq 8.10.2](https://coq.inria.fr/download)
- [SerAPI 0.7.0](https://github.com/ejgallego/coq-serapi)
- [Python 3](https://www.python.org)
- [JDK 8](https://openjdk.java.net) (or later)
- [Gradle 6](https://gradle.org/install/)
- [Git](https://git-scm.com)

## Installation and usage

We strongly recommend installing the required versions of OCaml, Coq,
and SerAPI via the [OPAM package manager](https://opam.ocaml.org),
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
the available options, use:
```
./mcoq.py --help
```

For example, to apply mCoq to [StructTact][structtact-repo]
revision [`82a85b7`][structtact-revision], use:
```
./mcoq.py --project StructTact --threads 2 --sha 82a85b7 \
  --url https://github.com/uwplse/StructTact.git \
  --buildcmd "./configure && make -j4" --qdir ".,StructTact"
```
After running this command, look for a HTML report in the `reports` directory.

For large Coq projects, it is recommended to set the `--threads` option
to at least the number of CPU cores in the machine, since mutation analysis
may otherwise take a long time to complete.

[ase-paper]: https://users.ece.utexas.edu/~gligoric/papers/CelikETAL19mCoq.pdf
[structtact-repo]: https://github.com/uwplse/StructTact
[structtact-revision]: https://github.com/uwplse/StructTact/commit/82a85b7ec07e71fa6b30cfc05f6a7bfb09ef2510

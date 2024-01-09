# `cljoss`

`cljoss` /kʎøs/ finds vulnerabilities in jars on a classpath
using the Sonatype OSS index API.

It's written in Clojure and it is primarily aimed at
Clojure projects, but it can analyse classpaths from
any language that runs on the JVM.

## Usage

This tool is available on Clojars.

Current recommended usage is to write a wrapper
script in your project, which builds the project 
classpath and calls `cljoss.core/run` with that
classpath and your chosen format.

Binaries, `lein` integrations, etc...
may become available in future releases.

## Goals

- Clear and understandable Clojure code
- Clear error messages when things go wrong

## Non-Goals

It is assumed that a tool like this will be run
in a project only infrequently in CI, e.g. nightly,
hence the following are not goals of this project

- Work avoidance
    - The tool won't try to cache any information or avoid fetching things again
- Blazing performance
    - The HTTP round trips to Sonatype's API will always be the bottleneck

## Development

This project uses Leiningen, so all the usual `lein` commands apply.

### Linting

The Leiningen plugin for clj-kondo seems to fall far behind clj-kondo
latest, so use of a local install is preferred. Linting in CI
will always be done with the latest version.

### Formatting

This project is formatted with 
[`cljfmt`](https://github.com/weavejester/cljfmt).
It's recommended to use the latest version of the binary,
which is how formatting will be checked in CI.

## Release

Until the release process is automated, to release

- read `CHANGELOG.md` and check the commits since the last
   release to understand the type (major/minor/patch)
- increase the version in `project.clj`, removing `SNAPSHOT`
- add a new section with the version into `CHANGELOG.md`
- commit & push to development
- merge this into `main`
- `git checkout main`
- `git pull -p`
- `lein release`
- `git checkout development`
- increase the patch version in `project.clj`
    and add `-SNAPSHOT` back to the end
- commit and push to development

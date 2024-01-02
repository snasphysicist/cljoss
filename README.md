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

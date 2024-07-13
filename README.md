# Storage

An interactive interface to a transactional key value store.

## Run

```
./gradlew run -q --console=plain
```

Type `HELP` to get a list of all supported commands.

## Run junit tests

```
./gradlew clean test
```

## Run e2e tests

```
./e2e-tests/run-tests.sh
```

Input data and expected output for e2e tests are located in `./e2e-tests/test-data`.
For example for the `set-get` test the input file is located
in `./e2e-tests/test-data/set-get/input.txt` and contains:

```
set foo 123
get foo
exit
```

and the expected output file is located in `./e2e-tests/test-data/set-get/output.txt`
and contains:

```
123
BYE!
```

In case where the test fails actual output can be found in the same 
folder: `./e2e-tests/test-data/set-get/output-actual.txt`

## Setup IDEA

In order to enable new kotlin 2.0 feature ExplicitBackingFields support in IDEA:

```
Settings -> Languages & Frameworks -> Kotlin: Check the "Enable K2 Kotlin Mode"
```

## License

    Copyright 2024 Dmitry Kalita.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

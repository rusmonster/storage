# Storage

An interactive CLI to a transactional key-value storage.

The storage is able to store and retrieve key-value pairs.
The storage supports nested transactions which can be started, committed or rolled back.

The storage uses transaction logs to rollback transactions.

Maximum transaction log capacity and maximum transaction depth are limited in order to avoid
memory leaks and to prevent the storage from being overloaded.

Use the `Storage.newStorage()` factory method to create a new instance of the storage.

The storage is not thread-safe by default. To make it thread-safe,
use the `Storage.synchronizedStorage()` method.

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

These e2e tests make the interface easily testable.

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

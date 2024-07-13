#!/bin/bash

REPO_DIR=`git rev-parse --show-toplevel`
TESTS_DATA_DIR="$REPO_DIR/e2e-tests/test-data"

RED='\033[0;31m'

function run_test() {
	TEST_NAME="$1"
	INPUT="$TESTS_DATA_DIR/$TEST_NAME/input.txt"
	EXPECTED_OUTPUT="$TESTS_DATA_DIR/$TEST_NAME/output.txt"
	ACTUAL_OUTPUT="$TESTS_DATA_DIR/$TEST_NAME/output-actual.txt"

	./gradlew run -q --console=plain < "$INPUT" | grep '^> *' | sed 's/> //' | grep -v '^[[:space:]]*$' > "$ACTUAL_OUTPUT"

	if cmp -s "$EXPECTED_OUTPUT" "$ACTUAL_OUTPUT"; then
		echo "$TEST_NAME: PASSED"
	else
		echo -e "$TEST_NAME: ${RED}FAILED"
	fi
}

cd "$REPO_DIR"

for dirname in $TESTS_DATA_DIR/*; do
	run_test "`basename "$dirname"`"
done

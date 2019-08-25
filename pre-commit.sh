#!/usr/bin/env bash

SAVEIFS=$IFS
IFS=$'\n'
CHANGED_FILES=($(git diff --cached --name-only --diff-filter=ACMRT | grep -E '\.java$'))
IFS=$SAVEIFS
unset SAVEIFS

FAILED=0

for CHANGED_FILE in "${CHANGED_FILES[@]}"; do
	git show ":${CHANGED_FILE}" | google-java-format --set-exit-if-changed --assume-filename="${CHANGED_FILE}" - >/dev/null 2>&1
	CHANGED=$?
	if [ $CHANGED -ne 0 ]; then
		echo "${CHANGED_FILE}: Not in Google format"
		FAILED=1
	fi
done

exit $FAILED

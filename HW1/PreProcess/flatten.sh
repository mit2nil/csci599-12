find */ -type f -exec bash -c 'file=${1#./}; mv "$file" "${file//\//_}"' _ '{}' \;
find */ -depth -type d -exec rmdir '{}' \;
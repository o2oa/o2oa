#!/bin/bash
function getdir(){
for element in `ls $1`
do
dir_or_file=$1"/"$element
if [ -d $dir_or_file ]
then
getdir $dir_or_file
else
echo $dir_or_file
    suffix="${dir_or_file##*.}"
    if [ "$suffix"x = "swift"x ]||[ "$suffix"x = "m"x ]||[ "$suffix"x = "mm"x ];
    then
        genstrings -a -o zh-Hans.lproj $dir_or_file
    fi
fi
done
}
root_dir="./"
getdir $root_dir

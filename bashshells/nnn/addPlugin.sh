#!/usr/bin/env sh

# Description: Update nnn plugins to installed nnn version
#
# Shell: POSIX compliant
# Authors: Arun Prakash Jana, KlzXS

CONFIG_DIR=${XDG_CONFIG_HOME:-$HOME/.config}/nnn/
PLUGIN_DIR=${XDG_CONFIG_HOME:-$HOME/.config}/nnn/plugins

merge () {
	if type nvim >/dev/null 2>&1; then
		nvim -d "$1" "$2"
	else
		vimdiff +0 "$1" "$2"
	fi
}

prompt () {
	printf "%s\n" "Plugin $1 already exists and is different."
	printf "Keep (k), merge (m), overwrite (o) [default: k]? "
	read -r operation

	if [ "$operation" = "m" ]; then
		op="merge"
	elif [ "$operation" = "o" ]; then
		op="cp -vRf"
	else
		op="true"
	fi
}

if [ "$1" = "master" ] ; then
    VER="master"
    ARCHIVE_URL=https://github.com/jarun/nnn/archive/master.tar.gz
elif type nnn >/dev/null 2>&1; then
    VER=$(nnn -V)
    ARCHIVE_URL=https://github.com/jarun/nnn/releases/download/v"$VER"/nnn-v"$VER".tar.gz
else
    echo "nnn is not installed"
    exit 1
fi

# backup any earlier plugins
if [ -d "$PLUGIN_DIR" ]; then
    tar -C "$CONFIG_DIR" -czf "$CONFIG_DIR""plugins-$(date '+%Y%m%d%H%M').tar.gz" plugins/
fi

mkdir -p "$PLUGIN_DIR"
cd "$CONFIG_DIR" || exit 1
curl -Ls "$ARCHIVE_URL" -o nnn-"$VER".tar.gz
tar -zxf nnn-"$VER".tar.gz

cd nnn-"$VER"/plugins || exit 1

# shellcheck disable=SC2044
# We do not use obnoxious names for plugins
for f in $(find . -maxdepth 1 \( ! -iname "." ! -iname "*.md" \)); do
	if [ -f ../../plugins/"$f" ]; then
		if [ "$(diff --brief "$f" ../../plugins/"$f")" ]; then
			prompt "$f"
			$op "$f" ../../plugins/
		fi
	else
		cp -vRf "$f" ../../plugins/
	fi
done
cd ../.. || exit 1

rm -rf nnn-"$VER"/ nnn-"$VER".tar.gz
#!/bin/sh
#<license>
#-------------------------------------------------------------------------------
# Copyright (c) 2007 ConocoPhillips Company
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#-------------------------------------------------------------------------------
#</license>
# $XFree86: xc/programs/scripts/xon.sh,v 1.10 2006/01/09 15:00:52 dawes Exp $
# start up xterm (or any other X command) on the specified host
# Usage: xon host [arguments] [command]

usage() {
    if [ -n "$1" ]
    then
	echo "`basename $0`: $1"
    fi
    echo ""
    echo "Usage:  `basename $0` hostname [options] [command ...]"
    echo ""
    echo "where options include:"
    echo "    -access         add remote host to local host access list"
    echo "    -debug          enable error messages from remote execution"
    echo "    -name name      set alternate application name and window title"
    echo "    -nols           do not pass -ls option to remote xterm"
    echo "    -remote cmd     use cmd to contact remote host"
    echo "    -screen screen  change remote screen number to specified screen"
    echo "    -user user      run remote command as the specified user"
    exit 1
}

if [ $# -eq 0 ]
then
    usage
fi

target=$1
shift
label=$target
resource=xterm-$label
if [ -f /usr/bin/remsh ]; then
    rsh=/usr/bin/remsh
elif [ -f /usr/bin/rcmd ]; then
    rsh=/usr/bin/rcmd
else
    rsh=rsh
fi
rcmd="$rsh $target -n"
case $DISPLAY in
unix:*)
	DISPLAY=`echo $DISPLAY | sed 's/unix//'`
	;;
esac
case $DISPLAY in
:*)
	case `uname` in
	Linux*)
		if [ -z "`hostname --version 2>&1 | grep GNU`" ]; then
			fullname=`hostname -f`
		else
			fullname=`hostname`
		fi
		;;
	*)
	fullname=`uname -n`
		;;
	esac
	hostname=`echo $fullname | sed 's/\..*$//'`
	if [ $hostname = $target ] || [ $fullname = $target ]; then
		DISPLAY=$DISPLAY
		rcmd="sh -c"
	else
		DISPLAY=$fullname$DISPLAY
	fi
	;;
esac
username=
sess_mangr=
xauth=
case x$XUSERFILESEARCHPATH in
x)
	xpath='HOME=${HOME-`pwd`} '
	;;
*)
	xpath='HOME=${HOME-`pwd`} XUSERFILESEARCHPATH=${XUSERFILESEARCHPATH-"'"$XUSERFILESEARCHPATH"'"} '
	;;
esac
redirect=" < /dev/null > /dev/null 2>&1 &"
command=
ls=-ls
continue=:
while $continue; do
	case $1 in
	-remote)
		shift
		if [ $rsh != "sh" ]; then
			rsh="$1"
			rcmd="$rsh $target -n"
		fi
		shift;;
	-user)
		shift

		if [ $# -eq 0 ]
		then
			usage "-user option requires an argument"
		fi

		username="-l $1"
		label="$target $1"
		rcmd="$rsh $target $username -n"
		shift
		case x$XAUTHORITY in
		x)
			XAUTHORITY="$HOME/.Xauthority"
			;;
		esac
		case x$XUSERFILESEARCHPATH in
		x)
			;;
		*)
			xpath="XUSERFILESEARCHPATH=$XUSERFILESEARCHPATH "
			;;
		esac
		;;
	-access)
		shift
		xhost +$target
		;;
	-name)
		shift

		if [ $# -eq 0 ]
		then
			usage "-name option requires an argument"
		fi

		label="$1"
		resource="$1"
		shift
		;;
	-nols)
		shift
		ls=
		;;
	-debug)
		shift
		redirect=
		;;
	-screen)
		shift

		if [ $# -eq 0 ]
		then
			usage "-screen option requires an argument"
		fi

		DISPLAY=`echo $DISPLAY | sed 's/:\\([0-9][0-9]*\\)\\.[0-9]/:\1/'`.$1
		shift
		;;
	*)
		continue=false
		;;
	esac
done
case x$XAUTHORITY in
x)
	;;
x*)
	xauth="XAUTHORITY=$XAUTHORITY "
	;;
esac
case x$SESSION_MANAGER in
x)
	;;
x*)
	sess_mangr="SESSION_MANAGER=$SESSION_MANAGER "
	;;
esac
vars='PATH=${PATH:+$PATH:}/usr/X11R6/bin '"$xpath$xauth$sess_mangr"DISPLAY="$DISPLAY"
case $# in
0)
	$rcmd 'sh -c '"'$vars"' xterm '$ls' -name "'"$resource"'" -T "'"$label"'" -n "'"$label"'" '"$redirect'"
	;;
*)
	$rcmd 'sh -c '"'$vars"' '"$*$redirect'"
	;;
esac

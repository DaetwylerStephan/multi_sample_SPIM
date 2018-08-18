#!/bin/bash

# maximum number of zips to run in parallel, plus one
MAX=14 # 14 means 13 zips => ensures max 1 per node (24 cores)

# start in the directory where the events are stored.
ZIP=$PWD/zip;

# name of this script
ME=$( basename $0 )

# search pattern
PAT="[${ME:0:1}]${ME:1}"

# log file
LOG=$ZIP/$( date +"%y%m%d-%H%M%S" ).log

# md5 file
MD5=$ZIP/md5

# default behavior
DRYRUN=false # no dry-run -> go fo real; override with -t
DELETE=false # don't delete original files; override with -d


# returns zip size (files and bytes); unzip -l counts directories as
# files and we need to filter that
function zipsize() {
   unzip -l $1 \
   | awk '
      BEGIN { f=0; n=0; s=0; }
      /^ *-----/ { f=1-f; next; }
      f && ! /\/$/ { n++; s=s+$1; }
      END { print n,s; }
   '
}

# return directory size (files and bytes)
function dirsize() {
   if [ $1 = "-p" ]
   then
      local pat="$2"
      shift 2
   else
      local pat="*"
   fi
   find $* ! -type d -name "$pat" -printf "%s\n" \
   | awk 'BEGIN { n=0; s=0; } { n++; s=s+$1; } END { print n,s; }'
}

# return a formatted dirsize()
function fmtdirsize() {
   dirsize $* | awk '
      # current awk integer size limits this function to around 8 PB
      function fmt(n) {
         b = "";
         while ( n > 999 ) {
            b = sprintf(",%03d%s",n%1000,b);
            n = int(n/1000);
         }
         return n b;
      }
      {
         l = split("Bytes KB MB GB TB PB",unit," ");
         s = $2;
         for ( i = 1; i <= l; i++ ) {
            if ( s < 1023 ) { break; }
            s = s / 1024;
         }
         printf("%s Bytes (%.2f %s), %s files\n",fmt($2),s,unit[i],fmt($1));
     }
   '
}

# compress a timepoint
function dozip() {
   local src=$1
   local dst=$2
   local abs=$ZIP/$dst
   local dot="$( dirname $abs )/.$( basename $abs )"
   mkdir -p $( dirname $abs )
   rm -f $dot # temporary files from previous executions can and must be removed
   local md5=$( zip -qr - $src | tee $dot | md5sum | cut -c-32 )
   local error=true
   if unzip -qt $dot &> /dev/null
   then
      # zip is not corrupted
      local out1=$( zipsize $dot )
      local out2=$( dirsize $src )
      if [ "$out1" = "$out2" ]
      then
         # zip contains as many files and bytes as the source directory
         if mv -i $dot $abs
         then
            # and it was possible to rename it
            echo "$dst: OK - $md5"
            echo "$md5  $dst" >> $MD5
            error=false
            if $DELETE
            then
               rm -rf $src
            fi
         fi
      fi
   fi
   if $error
   then
      rm $abs
      echo "$dst: ERROR CREATING ZIP FILE"
   fi
}


# parse command line options
while [ $# != 0 ]
do
   case $1 in
      -d ) DELETE=true
           ;;
      -h ) echo "$( basename $0 ) [-hdt]"
           echo "-h show this help"
           echo "-d delete files on successful zip creation"
           echo "-t dry-run (test mode)"
           exit
           ;;
      -t ) DRYRUN=true
           ;;
      -* ) echo "Ignoring unrecongnized flag: $1"
           ;;
      *  ) echo "Ignoring unrecognized parameter: $1"
   esac
   shift
done

# if there's no e??? directory to work with, there's nothing for this
# script to compress
if ! ls -d e??? &> /dev/null
then
   echo "Nothing to do. Exiting."
   exit
fi

# we'll need this later on, but we need to get it here if we're going
# to delete the original files
origsize="$( fmtdirsize e??? )"

# loop through all timepoints running $MAX-1 at a time
mkdir -p $( dirname $LOG )
total=0
for exp in e???
do 
   if cd $exp &> /dev/null
   then
      for sample in s*
      do
         if cd $sample &>/dev/null
         then
            for timepoint in t?????/
            do
               while [ $( ps -o ppid,comm | grep -c "^ *$$ $PAT$" ) -ge $MAX ]
               do
                  sleep 1
               done
               tp=${timepoint%/}
               zip=$exp/$sample/$tp.zip
               found=false
               if [ -e $ZIP/$zip ]
               then
                  # zip was found; if its md5 was already stored, it's ok
                  if grep  -E "^.{32}  $zip$" $MD5
                  then
                     found=true
                     echo "$zip: zip file found"
                  else
                     # something's wrong; remove zip
                     rm $ZIP/$zip
                  fi
               fi
               if ! $found
               then
                  if $DRYRUN
                  then
                     echo "Creating zip: $zip"
                  else
                     dozip $tp $zip &
                     sleep 1 # allow background process to initialize
                  fi
               fi >> $LOG
               let total=total+1
            done
            cd ..
            $DELETE && rmdir $sample 2> /dev/null
         fi
      done
      cd ..
      $DELETE && rmdir $exp 2> /dev/null
   fi
done

# wait for all background processes to finish
wait

# summary
(
   echo "PATH: $PWD"
   echo
   pat=": %${#total}d\n"
   printf "Already compressed$pat" $( grep -c " found$" $LOG )
   printf "Newly compressed  $pat" $( grep -c " OK " $LOG )
   printf "Failed            $pat" $( grep -c " ERROR " $LOG )
   echo
   echo "Original size  :" $origsize
   echo "Compressed size:" $( fmtdirsize -p "*.zip" zip )
   echo
   if $DRYRUN
   then
      echo "*** NO FILE WAS CREATED OR REMOVED (DRY RUN) ***"
      echo
   elif ! $DELETE
   then
      echo "*** ORIGINAL FILES WERE NOT REMOVED ***"
      echo
   fi
) | tee $LOG.tmp

# sort log file and append it to summary
sort $LOG >> $LOG.tmp
mv $LOG.tmp $LOG

# just so that no one wonders where the log file is
echo "See $LOG for details"

# sort md5 file
if [ -e $MD5 ]
then
   sort -k 2,2 $MD5 > $MD5.tmp
   mv $MD5.tmp $MD5
fi


#!/usr/bin/env python

"""Main mCoq script. Used to run mCoq tool and to generate mCoq reports.
   For examples on how to run, please see comments at bottom of this script. """
__author__ = "Kush Jain <kjain14@utexas.edu>"

import os
import argparse
import sys
import datetime
import subprocess

HIDE_SUFFIX = " > /dev/null 2>&1"
OPAM_UPDATE = "eval $(opam env);"
MCOQ_DIR = os.path.abspath("mcoq")
TOOL_JAR = os.path.join(MCOQ_DIR, "build/libs/mcoq-all-1.0-SNAPSHOT.jar")
COQ_VERSION = "8.10"
DOWNLOADS_DIR = "downloads"
RESULTS_DIR = "reports/results"

def setup_repo():
    curr_dir = os.getcwd()
    if not os.path.exists(DOWNLOADS_DIR):
        os.system("mkdir "+DOWNLOADS_DIR)
    os.chdir(DOWNLOADS_DIR)
    if os.path.exists(args.project):
        os.system("rm -rf "+args.project)
    print("Cloning repo "+args.project)
    os.system("git clone "+args.url+" "+args.project + HIDE_SUFFIX)
    os.chdir(args.project)
    os.system("git checkout "+args.sha + HIDE_SUFFIX)
    print("Building repo "+args.project)
    res = os.system(OPAM_UPDATE+args.buildcmd + HIDE_SUFFIX)
    if res != 0:
        print("Error building repo. Exiting now.")
        sys.exit(1)
    os.chdir(curr_dir)

def run_java(mcoq_mode):
    java_args = " --v810"
    if args.skipeq:
        java_args += " --skipeq"

    if not args.skipreport:
        java_args += " --debug"
        
    flags = ""
    if not args.qdir == "":
        flags += parse_flags(args.qdir, False)
    if not args.rdir == "":
        flags += parse_flags(args.rdir, True)
    java_args += " -e "+flags
    
    if not args.threads == "":
        java_args += " --threads "+args.threads
    if not args.mutator == "":
        java_args += " --mutator "+args.mutator
    else:
        java_args += " --mutator MutatorOneFileVO"
    java_args += " --project \"" + os.path.abspath(DOWNLOADS_DIR+"/"+args.project)+"\""
    final_command="java -jar "+TOOL_JAR+java_args
    get_results(final_command, mcoq_mode)

def get_results(command, mcoq_mode):
    curr_dir = os.getcwd()
    if not os.path.exists(RESULTS_DIR):
        os.system("mkdir "+RESULTS_DIR)
    os.chdir(RESULTS_DIR)
    if not os.path.exists(args.project):
        os.system("mkdir "+args.project)
    os.chdir(args.project)
    if not os.path.exists(mcoq_mode):
        os.system("mkdir "+mcoq_mode)
    os.chdir(mcoq_mode)
    if not os.path.exists(args.sha):
        os.system("mkdir "+args.sha)
    os.chdir(args.sha)

    LOG_FILE_NAME = "mcoq_log.txt"
    if os.path.exists(LOG_FILE_NAME):
        os.system("rm "+LOG_FILE_NAME)
    os.system("touch "+LOG_FILE_NAME)
    abs_path = os.path.abspath(LOG_FILE_NAME)
    os.chdir(curr_dir)
    os.chdir(DOWNLOADS_DIR+"/"+args.project)
    print("Running project "+args.project)
    exit_code = os.system(OPAM_UPDATE + command + " > "+abs_path)
    print("mCoq tool log available at "+abs_path)
    if exit_code != 0:
        print("Error running mCoq tool. Exiting now.")
        sys.exit(1)
    os.chdir(curr_dir)

def check_args():
    global args
    parser = argparse.ArgumentParser()
    parser.add_argument('--project', help='project to run report on', required=True)
    parser.add_argument('--nocheck', help='skip checking dependencies', action='store_true', default=False)
    parser.add_argument('--sha', help='sha of defined project', required=True)
    parser.add_argument('--report_dir', help='directory to generate reports in', default='')
    parser.add_argument('--buildcmd', help='coq build command', required=True)
    parser.add_argument('--url', help='url to clone project', required=True)
    parser.add_argument('--rdir', help= 'rdir for coq. Colon separated list that becomes coq rflags. For example .,StructTact:.,fcsl becomes --rflag .,StructTact --rflag .,fcsl', default="")
    parser.add_argument('--qdir', help='qdir for coq. Colon separated list that becomes coq flags. For example .,StructTact:.,fcsl becomes --flag .,StructTact --flag .,fcsl', default="" )
    parser.add_argument('--skipeq', help='skip equivelant mutants', default=False, action='store_true' )
    parser.add_argument('--threads', help='number of threads to run with mCoq.', default='')
    parser.add_argument('--dry', help='immediately return command to run java MCoq tool', default=False, action='store_true' )
    parser.add_argument('--mutator', help='which mode of mCoq we are running in', default="", choices={"MutatorOneFileVO", "MutatorOneFileVOOptOrder", "MutatorOneFileVOSkip", "MutatorOneFileVONoLeaves", "MutatorOneFileVOParCheck", "MutatorOneFileVOParMutant"} )
    parser.add_argument('--mutations', help='list of mutations to run separated by ,', default="" )
    parser.add_argument('--skipreport', help='run just the mCoq tool', action='store_true', default=False)
    parser.add_argument('--skipmutations', help='run just the reporting',action='store_true', default=False)

    try:
        args = parser.parse_args()
    except:
        if not "-h" in sys.argv and not "--help" in sys.argv:           
            print("ERROR: Missing required arguments. Please ensure you have passed correct arguments. List of required args shown above.")
        return False


    if args.mutator == "" and args.threads != "" and args.threads != "1":
        args.mutator = "MutatorOneFileVOParMutant"
    
    if args.qdir == "" and args.rdir == "":
        print("ERROR: Missing required arguments qdir/rdir. You need to specify one of these two arguments.")
        return False
    
    return True
    
def parse_flags(dir_vals, is_rdir):
    split_vals = dir_vals.split(":")
    flag_type = "rflag" if is_rdir else "flag"
    final_flags = ""
    for val in split_vals:
        if "," in val:
            curr_flag = "--"+flag_type+" \""+val.split(",")[0] + "," +val.split(",")[1]+"\""
        else:
            curr_flag = "--"+flag_type+" \""+val+","+val+"\""
        final_flags += curr_flag + " "
    return final_flags

def build_jar():
    curr_dir = os.getcwd()
    os.chdir(MCOQ_DIR)
    os.system("gradle fatJar" + HIDE_SUFFIX)
    os.chdir(curr_dir)
        
def gen_report(mcoq_mode):
    print("Generating report for project "+args.project)
    report_dir = ""
    reports_args = ""
    if args.report_dir != "":
        reports_args = " --report_dir "+os.path.abspath(args.report_dir)
    os.system("python3 log_parser.py --mode "+mcoq_mode + " --version "+COQ_VERSION +" --sha "+args.sha + reports_args +" --project "+args.project)

def check_dependencies():
    coq_check = check_command("coqc --version", "8.10")
    gradle_check = not os.system("gradle --version" + HIDE_SUFFIX)
    sercomp_check = check_command("sercomp --version", "0.7.0")
    git_check = not os.system("git --version" + HIDE_SUFFIX)
    java_check = check_java_version()
    
    if not coq_check:
        print("Missing coqc version 8.10. To install type: opam pin add coq 8.10.2")
    if not gradle_check:
        print("Missing gradle. To install, please follow the steps at: https://gradle.org/install/")
    if not sercomp_check:
        print("Missing sercomp version 0.7.0. To install type: opam pin add coq-serapi 8.10.0+0.7.0")
    if not git_check:
        print("Missing git. To install, please follow the steps at: https://git-scm.com/book/en/v2/Getting-Started-Installing-Git")
    
    if not (coq_check and gradle_check and sercomp_check and git_check and java_check):
        print("ERROR: Missing required dependencies. Please install to use tool (installation steps shown above). For a full list of installation steps, please refer to the README file.")
        return False
    return True

def check_opam_dependency(package, project):
    opam_check = check_command("opam list", package)
    if not opam_check:
        print("Missing "+package+". This is required to run "+project+".")
        sys.exit(1)
        
def check_command(command, version):
    # if no install
    if os.system(OPAM_UPDATE+command + HIDE_SUFFIX):
        return False
    try:
        res = subprocess.check_output(OPAM_UPDATE + command + " | grep -c \""+str(version)+"\"", shell=True)
        return True
    except:
        return False
    
def check_java_version():
    try:
        java_version = float(subprocess.check_output("java -version 2>&1 | awk -F[\\\"\.] -v OFS=. \'NR==1{print $2,$3}\'", shell=True))
    except:
        print("Java is not installed, please install JDK. To install, please follow the steps at: https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html")
        return False

    is_incorrect_version = java_version < 1.8 or (java_version > 2 and java_version < 8)
    
    if is_incorrect_version:
        print("Incorrect version of Java installed. Please install Java 8 or later. For installation steps, please refer to: https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html")
        return False
    return True

def main():
    dependencies_check = True
    if not "--nocheck" in sys.argv:
        dependencies_check = check_dependencies()
    args_check = check_args()
    success = dependencies_check and args_check    

    if not success:
        sys.exit(1)

    build_jar()
    
    MCOQ_MODE = "default_v810" if args.mutator == "" else args.mutator+"_v810"
    OLD_REPORT_DIR = os.path.abspath("./"+RESULTS_DIR+"/"+args.project+"/"+MCOQ_MODE+"/"+args.sha)

    if not args.skipreport:
        if os.path.exists(OLD_REPORT_DIR+"/mcoq-report"):
            os.system("rm -rf "+OLD_REPORT_DIR+"/mcoq-report")

    if not args.skipmutations:
        if os.path.exists(OLD_REPORT_DIR+"/mcoq_log.txt"):
            os.system("rm -rf "+OLD_REPORT_DIR+"/mcoq_log.txt")
    try:
        if not args.skipmutations:
            setup_repo()
            run_java(MCOQ_MODE)
        if not args.skipreport:
            gen_report(MCOQ_MODE)
    except:
        print("Error running mcoq script. Exiting now.")

if __name__ == '__main__':
    main()
        
# General examples for running mcoq.py
# ./mcoq.py --project [PROJECT NAME] --sha [PROJECT SHA] --url [PROJECT URL] --buildcmd [PROJECT CMD] --qdir [PROJECT QDIR] --rdir [PROJECT RDIR]

# Specific examples for running mcoq.py
# structtact - ./mcoq.py --project StructTact --sha 82a85b7ec07e71fa6b30cfc05f6a7bfb09ef2510 --url https://github.com/uwplse/StructTact.git --buildcmd "./configure && make -j4" --qdir ".,StructTact"
# To see list of all arguments type ./mcoq.py --help

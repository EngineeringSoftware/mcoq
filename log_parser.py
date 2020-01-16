import os
import argparse
import datetime

"""Main python reports script. Used to generate reports from log files"""
__author__ = "Kush Jain <kjain14@utexas.edu>"

parser = argparse.ArgumentParser()
parser.add_argument('--project', help='project to run report on', required=True)
parser.add_argument('--sha', help='sha of defined project', required=True )
parser.add_argument('--mode', help='mode mCoq is running', required=True )
parser.add_argument('--version', help='version of coq', required=True )
parser.add_argument('--report_dir', help='directory to make reports in', default='', required=False)
args = parser.parse_args()

RESULTS_DIR ="./reports/results"
PROJECT = args.project
SHA = args.sha
DATA_DIR = RESULTS_DIR + "/" + PROJECT + "/" + args.mode + "/" + SHA
FILES_DIR = os.getcwd()+"/downloads/" + PROJECT +  "/"
def score_data(data):
    score = round((data["num_killed"]/data["num_total"])*100, 1)
    data["mutation_score"] = str(score)+"%"
    data["color"] = compute_color(score)

def compute_color(mutation_score):
    if mutation_score == 100:
        return "green"
    elif mutation_score > 0:
        return "goldenrod"
    else:
        return "red"    

def gather_log_data():
    os.chdir(DATA_DIR)
    mutations_data = {}
    aggregate_data = {}
    mutations = set()
    FILE_NAME = "mcoq_log.txt"
    aggregate_data["num_killed"] = 0
    aggregate_data["num_total"] = 0
    with open(FILE_NAME) as f:
        lines = f.readlines()
        thread_line_map = {}
        for line in lines:
            if "DEBUG: Applied" in line:
                tid = line.split()[5]
                line_num = int(line.split("LINEB=")[1].split()[0])
                thread_line_map[tid] = line_num
                mutations.add(line.split()[2])
            elif "DEBUG: Results for" in line:
                mutation = line.split()[3]
                tid = line.split()[6]
                line_num = thread_line_map[tid]
                filename = line.split("in ")[1].split()[0]
                is_killed = (line.split("KILLED=")[1].split()[0] == "true")
                line_vals = {"operator":mutation, "line_num" : line_num, "killed": is_killed}
                if not filename in mutations_data:
                    new_file = {
                                "num_killed": 0, 
                                "num_total": 0,
                                "line_data": [],
                                "operator_data": {}
                                }
                    mutations_data[filename] = new_file
                file_data = mutations_data[filename]
                file_operators = file_data["operator_data"]
                if not mutation in file_operators:
                    op_default = {"num_killed": 0, "num_total": 0}
                    file_operators[mutation] = op_default
                curr_mutation = file_operators[mutation]
                if is_killed:
                    file_data["num_killed"] += 1
                    aggregate_data["num_killed"] += 1
                    curr_mutation["num_killed"]+=1

                file_data["num_total"] += 1
                aggregate_data["num_total"] += 1
                curr_mutation["num_total"]+=1

                score_data(file_data)
                score_data(curr_mutation)
                score_data(aggregate_data)

                aggregate_data["mutations"] = mutations                
                file_data["line_data"].append(line_vals)
            elif "Total number of timeouts" in line:
                aggregate_data["num_timeouts"] = line.split()[4]
            elif "Total number of equivalent mutants" in line:
                aggregate_data["num_equiv_mutants"] = line.split()[5]
            elif "Total sercomp time" in line:
                aggregate_data["sercomp_time"] = line.split()[3]
            elif "Total running time" in line:
                aggregate_data["running_time"] = line.split()[3]
            elif "Number of files per project" in line:
                aggregate_data["num_files"] = line.split()[5]
    return mutations_data, aggregate_data;

def gen_files_report(files):
    for filename in files:
        code_to_write = ''
        line_data = files[filename]['line_data']
        with open(FILES_DIR + filename) as f:
            lines = f.readlines()
            num_digits = len(str(len(lines)))
            for i in range(1, len(lines)+1):
                found_line = False
                op_map = {}
                num_killed = 0
                num_total = 0
                for line in line_data:
                    if line['line_num'] == i:
                        found_line = True
                        num_total+=1
                        operator = line['operator']
                        if operator in op_map:
                            op_map[operator]["num_total"] = op_map[operator]["num_total"] + 1
                        else:
                            op_map[operator] = {"num_killed":0,"num_total":1}
                        if line['killed']:
                            op_map[operator]["num_killed"] = op_map[operator]["num_killed"] + 1
                            num_killed += 1
                        
                if found_line:
                    color = "red" if num_killed < num_total else "green"
                    code_to_write += get_line_begin(i,num_digits)+"  <span style='background-color:"+color+"'>"+lines[i-1].rstrip('\n').replace("&","&amp;").replace(">","&gt;").replace("<","&lt;")+ "</span>"
                    code_to_write += " <p class='btn-link' style='display:inline' id="+str(i)+"-toggle onclick='toggleOperator("+str(i)+")'>View Operators</p>"
                    code_to_write += ' <div id='+str(i)+' style="display:none; margin-bottom:-20px">'+display_op_data(num_killed, num_total, op_map, get_spacing(num_digits, lines[i]))+"</div>"
                    code_to_write += '\n'
                else:
                    code_to_write += get_line_begin(i,num_digits)+"  "+lines[i-1].rstrip('\n')
                    code_to_write += '\n'
        with open(filename+".html", 'w') as f:
            #f.write(env.get_template("codeindex.html").render(filename=filename, project = PROJECT, code=code_to_write))
            f.write(generate_code_page(filename, code_to_write))

def get_spacing(num_digits, line):
    line_spacing = ''
    for i in range(num_digits):
        line_spacing+=' '
    line_spacing += '   '
    index = 0
    while line[index] == "" and index < len(line):
        line_spacing += ' '
    return line_spacing

def display_op_data(num_killed, num_total, op_map, spacing):
    lines_to_write = ''
    num_alive = num_total - num_killed
    keys = list(op_map.keys())
    for i in range(len(keys)):
        key = keys[i]
        op_totals = op_map[key]
        lines_to_write += spacing
        lines_to_write += "<span style='background-color:lightgray;'>" + key + ": " +str(op_totals["num_killed"])+"/"+str(op_totals["num_total"]) + "</span>"
        lines_to_write += '\n'
    lines_to_write += spacing
    lines_to_write += "<span style='background-color:lightgray;'>Total: "+str(num_killed)+"/"+str(num_total)+"</span>"
    lines_to_write += '\n'
    return lines_to_write

def get_line_begin(num, num_digits):
    line_begin = ''
    curr_digits = len(str(num))
    for i in range(num_digits-curr_digits):
        line_begin += ' '
    line_begin += str(num)
    line_begin += '.'
    return line_begin

def write_header_info(aggregate_data):
    lines_to_write = ''
    lines_to_write += write_line('<p style="margin:0"><b>SHA:</b> '+SHA+'</p>')
    lines_to_write += write_line('<p style="margin:0"><b>Report Time:</b> '+str(datetime.datetime.now())+'</p>')
    lines_to_write += write_line('<p style="margin:0"><b>Coq Version:</b> '+str(args.version)+'</p>')
    lines_to_write += write_line('<p style="margin:0"><b>mCoq Mode:</b> '+args.mode+'</p>')
    lines_to_write += write_line('<p style="margin:0"><b>Total Running Time (ms):</b> '+aggregate_data['running_time']+'</p>')
    lines_to_write += write_line('<p style="margin:0"><b>Total Sercomp Time (ms):</b> '+aggregate_data['sercomp_time']+'</p>')
    lines_to_write += write_line('<p style="margin:0"><b>Number of Timeouts:</b> '+aggregate_data['num_timeouts']+'</p>')
    lines_to_write += write_line('<p style="margin:0"><b>Number of Equivelent Mutants:</b> '+aggregate_data['num_equiv_mutants']+'</p>')
    lines_to_write += write_line('<p><b>Number of Files:</b> '+aggregate_data['num_files']+'</p>')

    return lines_to_write

def generate_table(aggregate_data, file_data):
    lines_to_write = ''
    lines_to_write += write_line('<!DOCTYPE html>')
    lines_to_write += write_line('<html>')
    lines_to_write += write_line('<head>')
    lines_to_write += write_line('<meta charset="UTF-8">')
    lines_to_write += write_line('<title>'+PROJECT+' Report</title>')
    lines_to_write += write_dependencies(False)
    lines_to_write += write_toggle()
    lines_to_write += write_line('<style> td, th { padding-top: 0 !important; padding-bottom: 0 !important; } </style>') 
    lines_to_write += write_line('</head>')
    lines_to_write += write_line('<body style="margin:8px">')
    # lines_to_write += write_sort_table()
    lines_to_write += write_line('<div style="border: #d6d3ce 1px solid;padding: 2px 4px 2px 4px;" id="breadcrumb">'+PROJECT+'</div>')
    lines_to_write += write_line('<h4 class="m-3" style="font-weight:bold;margin-left:0!important">mCoq report for '+PROJECT+'</h4>')
    lines_to_write += write_header_info(aggregate_data)
    lines_to_write += write_line('<table id="fileTable" class="table table-sm" style="max-width:900px;" id="table">')
    lines_to_write += write_table_header()
    lines_to_write += write_line('<tbody>')
    lines_to_write += write_aggregate_row(aggregate_data)
    lines_to_write += write_line('</tbody>')

    for single_file in sorted(file_data.keys()):
        curr_data = file_data[single_file]
        curr_ops = curr_data["operator_data"]
        lines_to_write += write_file_total(curr_data, single_file)
        lines_to_write += write_line('<tbody style="display:none" id="'+single_file.split('.')[0]+'">')
        for operator in sorted(aggregate_data["mutations"]):
            if operator in curr_ops:
                lines_to_write += write_file_operator(curr_ops[operator], operator)
            else:
                lines_to_write += write_empty_operator(operator)
        lines_to_write += write_line("</tbody>")
    lines_to_write += write_line('</table>')
    lines_to_write += write_footer()
    lines_to_write += write_line('</body>')
    lines_to_write += write_line('</html>')
    return lines_to_write

def write_toggle():
    lines_to_write = ''
    lines_to_write = write_line('<script>')
    lines_to_write += write_line('function toggleTableBody(id) {')
    lines_to_write += write_line('$("#"+id).toggle()')
    lines_to_write += write_line('if ($("#"+id+"-toggle").text() == "collapse") {')
    lines_to_write += write_line('$("#"+id+"-toggle").text("expand")')
    lines_to_write += write_line('}')
    lines_to_write += write_line('else {')
    lines_to_write += write_line('$("#"+id+"-toggle").text("collapse")')
    lines_to_write += write_line('}')

    lines_to_write += write_line('}')
    lines_to_write += write_line('</script>')
    return lines_to_write

def write_operator_toggle():
    lines_to_write = ''
    lines_to_write = write_line('<script>')
    lines_to_write += write_line('function toggleOperator(id) {')
    lines_to_write += write_line('$("#"+id).toggle()')
    lines_to_write += write_line('if ($("#"+id+"-toggle").text() == "Hide Operators") {')
    lines_to_write += write_line('$("#"+id+"-toggle").text("View Operators")')
    lines_to_write += write_line('}')
    lines_to_write += write_line('else {')
    lines_to_write += write_line('$("#"+id+"-toggle").text("Hide Operators")')
    lines_to_write += write_line('}')

    lines_to_write += write_line('}')
    lines_to_write += write_line('</script>')
    return lines_to_write

# def write_sort_table():
#     lines_to_write = ''
#     lines_to_write = write_line('<script>')
#     lines_to_write += write_line('$(document).ready(function () {')
#     lines_to_write += write_line('$("#fileTable").DataTable()')
#     lines_to_write += write_line('$(".dataTables_length").addClass("bs-select")')
#     lines_to_write += write_line('});')
#     lines_to_write += write_line('</script>')
#     return lines_to_write


def write_aggregate_row(aggregate_data):
    lines_to_write =  write_table_row(
        '<b>All files</b>', 
        '<b>Total for '+PROJECT+'</b>',
        aggregate_data["mutation_score"],
        aggregate_data["num_total"],
        aggregate_data["num_killed"],
        aggregate_data["color"],
        False)
    lines_to_write += "<tr><td></td><td></td><td></td><td></td><td></td></tr>"
    return lines_to_write

def write_file_total(file_data, file):
    toggle_name = file.split('.')[0]
    return write_table_row(
        '<a href="files/'+file+'.html">'+file+'</a>',
        'All (<p style="display:inline-block;margin:0" id="'+toggle_name+'-toggle" class="btn-link" onclick="toggleTableBody(\''+toggle_name+'\')">expand</p>)',
        file_data["mutation_score"],
        file_data["num_total"],
        file_data["num_killed"],
        file_data["color"],
        False
    )

def write_file_operator(op_data, operator):
    return write_table_row(
        '',
        operator,
        op_data["mutation_score"],
        op_data["num_total"],
        op_data["num_killed"],
        op_data["color"],
        True
    )

def write_empty_operator(operator):
    lines_to_write = ''
    lines_to_write += write_line('<tr>')
    lines_to_write += write_line('<td style="border:0"></td>')
    lines_to_write += write_line('<td>'+operator+'</td>')
    lines_to_write += write_line('<td style="vertical-align: middle; padding-right:2.2rem">')
    lines_to_write += write_line('<div class="progress" id="percentage" style="max-width: 15rem">')
    lines_to_write += '<div class="progress-bar" style="width: 0.0%"></div>'
    lines_to_write += '\n'
    lines_to_write += write_line("</div>")
    lines_to_write += write_line('</td>')
    lines_to_write += write_line('<td style="text-align:right"> - </td>') 
    lines_to_write += write_line('<td style="text-align:right"> - </td>')
    lines_to_write += write_line('</tr>')
    return lines_to_write

def write_table_row(file, operator, score, total, killed, color, noborder):
    lines_to_write = ''
    lines_to_write += write_line('<tr>')
    if noborder:
        lines_to_write += write_line('<td style="border:0">'+file+'</td>')
    else:
        lines_to_write += write_line('<td>'+file+'</td>')
    lines_to_write += write_line('<td>'+operator+'</td>')
    lines_to_write += write_line('<td style="vertical-align: middle; padding-right:2.2rem">')
    lines_to_write += write_line('<div class="progress" id="percentage" style="max-width: 15rem">')
    if score == "0.0%":
        lines_to_write += write_line('<div class="progress-bar" style="width: 100%; background:'+color+'">')
        lines_to_write += '\n'
        lines_to_write += write_line(score)
        lines_to_write += write_line("</div>")

    elif score == "100.0%":
        lines_to_write += write_line('<div class="progress-bar" style="width: 100%; background:'+color+'">')
        lines_to_write += '\n'
        lines_to_write += write_line(score)
        lines_to_write += write_line("</div>")

    else:
        lines_to_write += '<div class="progress-bar" style="width: '+score+';background:'+color+'">'
        lines_to_write += '\n'
        lines_to_write += write_line(score)
        lines_to_write += write_line("</div>")
    lines_to_write += write_line('</div>')
    lines_to_write += write_line('</td>')
    lines_to_write += write_line('<td style="text-align:right">'+str(total)+'</td>') 
    lines_to_write += write_line('<td style="text-align:right">'+str(killed)+'</td>')
    # lines_to_write += write_line('<td style="text-align:center">'+btn_text+'</td>')
    lines_to_write += write_line('</tr>')
    return lines_to_write

def write_zero_bar():
    lines_to_write = ''
    # lines_to_write += write_line('<div class="col-5" style="padding:0">')
    # lines_to_write += write_line('<p style="color:red;margin: 0;padding-left: 0;">_____________________________</p>')
    # lines_to_write += write_line('</div>')
    lines_to_write += write_line('<div class="col" style="padding-left:0.3rem;color:red">0.0%</div>')
    # lines_to_write += write_line('<div class="col-6" style="padding:0;">')
    # lines_to_write += write_line('<p style="color:red;margin: 0;padding-left: 0;">_____________________________</p>')
    # lines_to_write += write_line('</div>')
    return lines_to_write

def write_table_header():
    lines_to_write = ''
    lines_to_write += write_line('<thead>')
    lines_to_write += write_line('<tr style="background-color:lightgray">')
    lines_to_write += write_line('<th>File Name</th>')
    lines_to_write += write_line('<th style="min-width:223px">Mutation Operator</th>')
    lines_to_write += write_line('<th>Mutation Score</th>')
    lines_to_write += write_line('<th style="text-align:right">Generated</th>') 
    lines_to_write += write_line('<th style="text-align:right">Killed</th>')
    # lines_to_write += write_line('<th style="min-width:72px"></th>')
    lines_to_write += write_line('</tr>')
    lines_to_write += write_line('</thead>')
    return lines_to_write

def generate_code_page(filename, code):
    lines_to_write = ''
    lines_to_write += write_line('<!DOCTYPE html>')
    lines_to_write += write_line('<html>')
    lines_to_write += write_line('<head>')
    lines_to_write += write_line('<meta charset="UTF-8">')
    lines_to_write += write_line('<title>'+filename+' Report</title>')
    lines_to_write += write_dependencies(True)
    lines_to_write += write_operator_toggle()
    lines_to_write += write_line('<script> hljs.initHighlightingOnLoad(); </script>') 
    lines_to_write += write_line('</head>')
    lines_to_write += write_line('<body style="margin:8px">')
    lines_to_write += write_line('<div style="border: #d6d3ce 1px solid;padding: 2px 4px 2px 4px;" id="breadcrumb">')
    lines_to_write += write_line('<a href="../index.html" class="el_report">'+PROJECT+'</a>')
    lines_to_write += write_line('&gt; '+filename)
    lines_to_write += write_line('</div>')
    lines_to_write += write_line('<h4 class="m-3" style="font-weight:bold;margin-left:0!important">'+filename+'</h4>')
    lines_to_write += write_line('<pre style="white-space: pre-wrap">')
    lines_to_write += write_line('<code class="Coq">'+code+'</code>')
    lines_to_write += write_line('</pre>')
    lines_to_write += write_footer()
    lines_to_write += write_line('</body>')
    lines_to_write += write_line('</html>')
    return lines_to_write

def write_footer():
    lines_to_write = ''
    lines_to_write += '<div style="margin-top: 20px; border-top: #d6d3ce 1px solid; padding-top: 2px; font-size: 8pt;'
    lines_to_write += 'color: #a0a0a0;">'
    lines_to_write += '\n'
    lines_to_write += write_line('<span class="float-right">')
    lines_to_write += write_line('Created with mCoq 1.1')
    lines_to_write += write_line('</span>')
    lines_to_write += write_line('Mutation Testing Report for mCoq 1.1</div>')
    return lines_to_write

def write_dependencies(is_file_level):
    lines_to_write = ''
    root_dir = '..' if is_file_level else '.'
    lines_to_write += write_line('<link rel="stylesheet" href="'+root_dir+'/resources/bootstrap/css/bootstrap.min.css">')
    lines_to_write += write_line('<link rel="stylesheet" href="'+root_dir+'/resources/highlight/styles/default.css">')
    lines_to_write += write_line('<script src="'+root_dir+'/resources/highlight/highlight.pack.js"></script>')
    lines_to_write += write_line('<script src="'+root_dir+'/resources/bootstrap/js/jquery.min.js"></script>')
    # lines_to_write += write_line('<script src="'+root_dir+'/resources/bootstrap/js/jquery.dataTables.min.js"></script>')
    lines_to_write += write_line('<script src="'+root_dir+'/resources/bootstrap/js/bootstrap.min.js"></script>')
    return lines_to_write
    
def write_line(content):
    line = ''
    line += content
    line += '\n'
    return line

def main():
    orig_dir = os.getcwd()
    report_data, project_data = gather_log_data()
    if args.report_dir != "":
        os.chdir(args.report_dir)
    else:
        os.chdir(orig_dir+"/"+RESULTS_DIR+"/"+args.project+"/"+args.mode+"/"+args.sha)
    if os.path.exists("mcoq-report"):
        os.system("rm -rf mcoq-report")
    os.system("mkdir mcoq-report")
    os.chdir("mcoq-report")
    print("Report available at "+os.getcwd()+"/index.html")
    os.system("mkdir files")
    os.system("touch index.html")
    os.system("cp -r "+orig_dir+"/reports/resources ./resources")
    root_dir = os.getcwd()

    with open("index.html", 'w') as f:
        f.write(generate_table(project_data, report_data))
    os.chdir(root_dir+"/files")
    gen_files_report(report_data)




if __name__ == '__main__':
    #try:
    main()
    #except:
        #print("Error generating report. Exiting now.")

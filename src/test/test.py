import os, glob, shutil, time
test_case_path = os.path.abspath(".\\testcases")
word_dir = os.path.abspath(".")
root_path = os.path.abspath("..\\..\\")
request_files = glob.glob(os.path.join(test_case_path, "request*.json"))

results_path = os.path.join(word_dir, f"result{time.time()}")
os.makedirs(results_path, exist_ok=False)

os.chdir(root_path)
for file in request_files:
    shutil.copy(file, os.path.join(root_path, "request.json"))
    cmd_str = "gradle run " + "> " + results_path + "\\" + file.split("\\")[-1] + " 2>&1"
    print(cmd_str)
    os.system(cmd_str)  
print(request_files)
import numpy as np
import util
import os
import subprocess
import pickle

'''
Run PMD script over all submissions, creates a dictionary with it and dumps its oautput into a .pkl file.

PDM is a tool used to identify various style issues with code. It also comes installed wtih CPD, a tool to
identify repeated sections of code. These scripts allow us to extract some useful information from the submissions,
but since they take a long time to run, we created this file to do it once and save the output in a .pkl file, so
that we don't have to run it all the time.

TODO: do the same for the PMD script (for now, only CPD).
TODO: review choice of # of tokens (is 15 a good choice?)
'''

results = dict()

assignment_ids, _ = util.get_data('Decomposition')

for a_id in assignment_ids:
	file_path = '/'.join(['./data/files/', a_id, 'Breakout.java'])

	bash_command = './pmd-bin-6.9.0/bin/run.sh cpd --minimum-tokens 5 --files {}'.format(file_path)
	process = subprocess.Popen(bash_command.split(), stdout=subprocess.PIPE)
	out, error = process.communicate()
	results[a_id] = str(out)

output = open('data.pkl', 'wb')
pickle.dump(results, output)
output.close()

# assignment_ids, _ = util.get_data('Naming and Spacing')

# results = dict()

# for a_id in assignment_ids:
# 	file_path = '/'.join(['./data/files/', a_id, 'Breakout.java'])

# 	bash_command = './pmd-bin-6.9.0/bin/run.sh pmd -d {} -f text -R category/java/codestyle.xml'.format(file_path).format(file_path)
# 	process = subprocess.Popen(bash_command.split(), stdout=subprocess.PIPE)
# 	out, error = process.communicate()
# 	results[a_id] = str(out)

# output = open('data_pmd.pkl', 'wb')
# pickle.dump(results, output)
# output.close()

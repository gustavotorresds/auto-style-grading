import numpy as np
import util
import os
import subprocess
import pickle

results = dict()

for quarter_id in ['1222', '1278', '1363']:
	assignment_ids, _ = util.get_data(quarter_id, 'Decomposition')

	for a_id in assignment_ids:
		file_path = '/'.join(['./data/files/', a_id, 'Breakout.java'])

		bash_command = "./pmd-bin-6.9.0/bin/run.sh cpd --minimum-tokens 15 --files {}".format(file_path)
		process = subprocess.Popen(bash_command.split(), stdout=subprocess.PIPE)
		out, error = process.communicate()
		results[a_id] = str(out)

output = open('data.pkl', 'wb')
pickle.dump(results, output)
output.close()
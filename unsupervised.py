import matplotlib.pyplot as plt
import seaborn as sns; sns.set()  # for plot styling
import numpy as np
import util
import os
import subprocess
import pickle

from sklearn.cluster import KMeans
from sklearn.manifold import TSNE

K = 3

def main():
	pmd_reports = load_pkl_file('data.pkl')

	bucket = 'Decomposition'
	assignment_ids, y = util.get_data(bucket)

	X = np.array([extract_features(assignment_id, bucket, pmd_reports[assignment_id]) for assignment_id in assignment_ids])
	X_embedded = TSNE(n_components=2).fit_transform(X)

	kmeans = KMeans(n_clusters=K, random_state=0).fit_predict(X_embedded)

	for cluster in range(K):
		buckets = {1: 0, 2: 0, 3: 0}
		for i, label in enumerate(y):
			if kmeans[i] == cluster:
				buckets[label] += 1
		s = buckets[1] + buckets[2] + buckets[3]
		print('CONCENTRATIONS in cluster #{}'.format(cluster))
		print('Major Issues: {}'.format(buckets[1] / s))
		print('Minor Issues: {}'.format(buckets[2] / s))
		print('Perfect: {}'.format(buckets[3] / s))

	plt.scatter(X_embedded[:, 0], X_embedded[:, 1], c=kmeans, cmap='viridis', alpha=0.9);
	plt.show()


def load_pkl_file(filename):
	pkl_file = open(filename, 'rb')
	pmd_reports = pickle.load(pkl_file)
	pkl_file.close()
	return pmd_reports

def extract_features(assignment_id, bucket, report=None):
	file_path = '/'.join(['./data/files', assignment_id, 'Breakout.java'])
	file = util.open_file(file_path)
	file_lines = [l for l in file]
	
	if bucket == 'Decomposition':
		return decomposition_features(file_lines, report)
	# TODO: implement feature extraction for other buckets.
	elif bucket == 'Naming and Spacing':
		return naming_and_spacing_features(file_lines, report)
	else:
		print('Can\'t read that bucket yet :/')

	return []

'''
Input: a compilable Java progrma
Output: an array containing
- # of lines in file
- # of methods in file
- average # of lines per method
'''
def decomposition_features(file, report):
	# TODO: check if this actually workds. It probably has some untouched edge cases,
	# but it seems to work for most cases.
	def is_a_method(line):
		return (('private' in line or 'public' in line) and \
			'(' in line and \
			')' in line and \
			'=' not in line)

	def get_method_counts(file):
		method_line_count = 0 # Tracks the number of lines in the method we are in.
		bracket_counter = 0 # If we are inside a method, bracket counter is 0 only when we are in the last line of that method
		method_line_count_mode = False # Tracks whether we are inside a method (and hence whether we are counting its number of lines
		method_line_counts = [] # An array that stores the number of lines in each of the file methods.

		for line in file: # Are we in method-line-count mode?
			if method_line_count_mode: # If so, we gotta check if this line is the end of the method we are in.
				open_brackets = line.count('{')
				close_brackets = line.count('}')

				bracket_counter = bracket_counter + open_brackets - close_brackets

				if bracket_counter == 0: # This the end of the method!
					method_line_counts.append(method_line_count)
					method_line_count = 0
					method_line_count_mode = False
				else:
					method_line_count += 1
			else:
				if is_a_method(line): # Is this line the start of a method?
					method_line_count_mode = True # Get into method-line-count mode
					bracket_counter = 1
					method_line_count = 0

		return method_line_counts

	def get_run_method_length(file):
		run_line_count = 0 # Tracks the number of lines in the method we are in.
		bracket_counter = 0 # If we are inside a method, bracket counter is 0 only when we are in the last line of that method
		in_run = False

		for line in file: # Are we in method-line-count mode?
			if in_run:
				open_brackets = line.count('{')
				close_brackets = line.count('}')
				bracket_counter = bracket_counter + open_brackets - close_brackets

				if bracket_counter == 0: # This the end of the method!
					in_run = False
					break
				else:
					run_line_count += 1
			else:
				if 'public' in line and  'void' in line and 'run' in line:
					in_run = True

					open_brackets = line.count('{')
					close_brackets = line.count('}')
					bracket_counter = bracket_counter + open_brackets - close_brackets

		return run_line_count

	def get_line_count(file):
		line_count = 0
		for line in file:
			line_count += 1
		return line_count

	'''
	Output of PMD reports is:

		Found a 7 line (110 tokens) duplication in the following files:
		  Starting at line 579 of /home/me/src/test/java/foo/FooTypeTest.java
		  Starting at line 586 of /home/me/src/test/java/foo/FooTypeTest.java

		Found a 5 line (...)

	Hence, we just count the number of 'Found a ' appearences.
	TODO: make this more sophisticated! There may be a lot more to extract from here.
	'''
	def get_repetitions(report):
		num_repetitions = report.count('Found a ')
		return num_repetitions

	line_count = get_line_count(file)
	method_counts = get_method_counts(file)
	num_repetitions = get_repetitions(report)
	run_length = get_run_method_length(file)

	return [line_count, len(method_counts), np.mean(method_counts), np.max(method_counts), np.min(method_counts), num_repetitions, run_length]

if __name__ == "__main__":
	main()

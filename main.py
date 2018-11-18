import numpy as np
import util
import os

def main():
	assignment_ids, _ = util.load_data('./data/grades/1222.csv')

	bucket = 'Decomposition'
	labels = util.generate_labels_for_bucket('./data/grades/1222.csv', bucket)

	X = np.array([extract_features(assignment_id, bucket) for assignment_id in assignment_ids])
	print(X)

def svm():
	pass

def naive_bayes():
	pass

def extract_features(assignment_id, bucket):
	file_path = '/'.join(['./data/files/1222', assignment_id, 'Breakout.java'])
	file = util.open_file(file_path)
	
	if bucket == 'Decomposition':
		return decomposition_features(file)

	return []

'''
[Work in Progress]
Input: a compilable Java progrma
Output: an array with containing
- # of lines in file
- # of methods in file
- # Average of # lines per method
'''
def decomposition_features(file):
	def is_a_method(line):
		return False

	line_count = 0
	method_count = 0

	# Tracks the number of lines in 
	method_line_count_helper = 0
	bracket_counter = 0

	for line in file:
		line_count += 1

		# Are we in method-line-count mode?
			# If so, is this the end of the method?
				# If so, set method count and count # lines per method, and reset variables
				# If not, just count +1
			# Otherwise, is this line the start of a method?
				# If it is, get into method-line-count mode

	return [line_count, 0., 0.]


if __name__ == "__main__":
	main()

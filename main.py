import numpy as np
import util
import os
from sklearn.naive_bayes import GaussianNB

def main():
	assignment_ids, _ = util.load_data('./data/grades/1222.csv')

	bucket = 'Decomposition'

	y = util.generate_labels_for_bucket('./data/grades/1222.csv', bucket)
	X = np.array([extract_features(assignment_id, bucket) for assignment_id in assignment_ids])

	split_index = int(len(assignment_ids) * .8)
	trainX, testX = X[split_index:], X[:split_index]
	trainY, testY = y[split_index:], y[:split_index]

	clf = GaussianNB()
	clf.fit(trainX, trainY)

	print(clf.score(testX, testY))

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
	# TODO: check if this actually workds. It probably has some untouched edge cases,
	#  but it seems to work for most cases.
	def is_a_method(line):
		return (('private' in line or 'public' in line) and \
			'(' in line and \
			')' in line and \
			'=' not in line)

	line_count = 0

	'''Tracks the number of lines in the method we are in.'''
	method_line_count = 0 

	'''If we are inside a method, bracket counter is 0 only when we are in the last
	line of that method.'''
	bracket_counter = 0

	'''Tracks whether we are inside a method (and hence whether we are counting its
	number of lines)'''
	method_line_count_mode = False

	'''An array that stores the number of lines in each of the file methods.'''
	method_line_counts = []

	for line in file:
		line_count += 1

		# Are we in method-line-count mode?
		if method_line_count_mode:
			# If so, we gotta check if this line is the end of the method we are in.
			open_brackets = line.count('{')
			close_brackets = line.count('}')

			bracket_counter = bracket_counter + open_brackets - close_brackets

			if bracket_counter == 0: # This the end of the method!
				method_line_counts.append(method_line_count_helper)
				method_line_count = 0
				method_line_count_mode = False
			else:
				method_line_count += 1
		else:
			# Is this line the start of a method?
			if is_a_method(line):
				# Get into method-line-count mode
				method_line_count_mode = True
				bracket_counter = 1
				method_line_count_helper = 0

	num_methods = len(method_line_counts)
	lines_per_method = np.mean(method_line_counts)

	return [line_count, num_methods, lines_per_method]

if __name__ == "__main__":
	main()

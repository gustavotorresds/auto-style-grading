import numpy as np
import util
import os

def main():
	assignment_ids, _ = util.load_data('./data/grades.csv')
	bucket = 'Decomposition'
	labels = util.generate_labels_for_bucket('./data/grades.csv', bucket)

	x = extract_features(assignment_ids[0], bucket)
	print(x)
	# X = np.array([extract_features(assignment_id, 'Decomposition') for assignment_id in assignment_ids])
	# print(X)

	# Do something with grades, e.g., save as .csv
	# np.savetxt('./output/labels.txt', labels)

def svm():
	pass

def naive_bayes():
	pass

def extract_features(assignment_id, bucket):
	file_path = '/'.join(['./data/files', assignment_id, 'Breakout.java'])
	file = util.open_file(file_path)
	
	if bucket == 'Decomposition':
		return decomposition_features(file)

	return []

'''
Input: a compilable Java progrma
Output: an array with containing
- # of lines in file
- # of methods in file
- # Average of # lines per method
'''
def decomposition_features(file):
	line_count = 0
	method_count = 0

	for line in file:
		line_count += 1

	return [line_count, 0., 0.]


if __name__ == "__main__":
	main()

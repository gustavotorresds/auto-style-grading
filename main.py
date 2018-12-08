import numpy as np
import util
import os
import subprocess
import pickle
from sklearn.naive_bayes import GaussianNB
from sklearn.linear_model import LogisticRegression

def main():
	pmd_reports = load_pkl_file()

	bucket = 'Decomposition'
	assignment_ids_1, y_1 = util.get_data('1222', bucket)
	assignment_ids_2, y_2 = util.get_data('1278', bucket)
	assignment_ids_3, y_3 = util.get_data('1363', bucket)

	assignment_ids = assignment_ids_1 + assignment_ids_2 + assignment_ids_3
	y = y_1 + y_2 + y_3

	X = np.array([extract_features(assignment_id, bucket, pmd_reports[assignment_id]) for assignment_id in assignment_ids])
	split_index = int(len(assignment_ids) * .9)
	xTrain, xTest = X[:split_index], X[split_index:]
	yTrain, yTest = y[:split_index], y[split_index:]
	naive_bayes(xTrain, yTrain, xTest, yTest)
	logistic_regression(xTrain, yTrain, xTest, yTest)

	# bucket = 'Naming and Spacing'

	# y = util.generate_labels_for_bucket('./data/grades/1222.csv', bucket)
	# X = np.array([extract_features(assignment_id, bucket) for assignment_id in assignment_ids])

	# split_index = int(len(assignment_ids) * .9)
	# xTrain, xTest = X[:split_index], X[split_index:]
	# yTrain, yTest = y[:split_index], y[split_index:]

	# naive_bayes(xTrain, yTrain, xTest, yTest)
	# logistic_regression(xTrain, yTrain, xTest, yTest)

	# split_index = int(len(assignment_ids) * .9)
	# xTrain, xTest = X[:split_index], X[split_index:]
	# yTrain, yTest = y[:split_index], y[split_index:]

	# naive_bayes(xTrain, yTrain, xTest, yTest)
	# logistic_regression(xTrain, yTrain, xTest, yTest)

def load_pkl_file():
	pkl_file = open('data.pkl', 'rb')
	pmd_reports = pickle.load(pkl_file)
	pkl_file.close()
	return pmd_reports

def svm():
	pass

def train_and_test(clf, xTrain, yTrain, xTest, yTest, model_name='model_name'):
	clf.fit(xTrain, yTrain)
	print('Score on Train Set is: ', clf.score(xTrain, yTrain))
	print('Score on Test Set is: ', clf.score(xTest, yTest))

	predictions = np.array(clf.predict(xTest))
	np.savetxt('./output/{}_labels.txt'.format(model_name), predictions)

def naive_bayes(xTrain, yTrain, xTest, yTest):
	print('Training on Naive Bayes')
	clf = GaussianNB()
	train_and_test(clf, xTrain, yTrain, xTest, yTest, 'nb')

def logistic_regression(xTrain, yTrain, xTest, yTest):
	print('Training on Logistic Regression')
	clf = LogisticRegression(solver='lbfgs', multi_class='multinomial', max_iter=10000)
	train_and_test(clf, xTrain, yTrain, xTest, yTest, 'lr')

def extract_features(assignment_id, bucket, report):
	file_path = '/'.join(['./data/files', assignment_id, 'Breakout.java'])
	file = util.open_file(file_path)
	file_lines = [l for l in file]
	
	if bucket == 'Decomposition':
		return decomposition_features(file_lines, file_path, report)
	# TODO: implement feature extraction for other buckets.
	elif bucket == 'Naming and Spacing':
		return naming_and_spacing_features(file)
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
def decomposition_features(file, assignment_id, report):
	# TODO: check if this actually workds. It probably has some untouched edge cases,
	# but it seems to work for most cases.
	def is_a_method(line):
		return (('private' in line or 'public' in line) and \
			'(' in line and \
			')' in line and \
			'=' not in line)

	def get_method_counts(file):
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
			# Are we in method-line-count mode?
			if method_line_count_mode:
				# If so, we gotta check if this line is the end of the method we are in.
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
				# Is this line the start of a method?
				if is_a_method(line):
					# Get into method-line-count mode
					method_line_count_mode = True
					bracket_counter = 1
					method_line_count = 0

		return method_line_counts

	def get_line_count(file):
		line_count = 0
		for line in file:
			line_count += 1
		return line_count

	def get_repetitions(report):
		num_repetitions = report.count('Found a ')
		return num_repetitions

	line_count = get_line_count(file)
	method_counts = get_method_counts(file)
	num_repetitions = get_repetitions(report)

	return [line_count, len(method_counts), np.mean(method_counts), np.max(method_counts), np.min(method_counts), num_repetitions]

'''
Input: a compilable Java progrma
Output: an array containing
- # of variables with lowerCamelCase
- # of lines with wrong indentation
'''
def naming_and_spacing_features(file):
	'''Removes characters that might have been attached to variable name'''
	def variable_filter(var):
		var = var.replace(';', '')
		var = var.replace('=', '')
		var = var.replace(',', '')

		if '(' in var:
			argContent = var[var.index('('):]
			var = var.replace(argContent, '')

		return var

	def get_variable(line):
		if ('static' not in line and 'final' not in line):
			tokens = line.split()

			returnNext = False

			for token in tokens:
				if returnNext:
					return variable_filter(token)
				if token in util.VAR_TYPES:
					returnNext = True
		return None

	def is_camel_case(var):
		if not var[0].islower():
			return False
		if '_' in var:
			return False
		return True

	def has_right_indentation(line, indentation_level):
		if line.strip() == '':
			return True

		i = 0
		while line[i] == '\t':
			i += 1
		return i == indentation_level

	indentation_level = 0

	camel_case_count = 0
	wrong_indentation_count = 0

	for line in file:
		var = get_variable(line)
		if var and is_camel_case(var):
			camel_case_count += 1

		if '}' in line:
			indentation_level -= 1

		if not has_right_indentation(line, indentation_level):
			wrong_indentation_count += 1

		if '{' in line:
			indentation_level += 1

	return [camel_case_count, wrong_indentation_count]

if __name__ == "__main__":
	main()

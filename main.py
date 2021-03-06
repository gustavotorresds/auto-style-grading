import numpy as np
import util 
import os
import collections
from sklearn.naive_bayes import GaussianNB
from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC

def main():
	assignment_ids, _ = util.load_data('./data/grades/1222.csv')
	buckets = []

	buckets.append('Decomposition')
	buckets.append('Commenting')
	#buckets.append('Naming and Spacing')

	y = util.generate_labels_for_buckets('./data/grades/1222.csv', buckets)
	X = np.array([extract_features(assignment_id, buckets) for assignment_id in assignment_ids])

	split_train_prop = 0.7 # propotion of data used for training
	split_index = int(len(assignment_ids) * split_train_prop)
	xTrain, xTest = X[:split_index], X[split_index:]
	yTrain, yTest = y[:split_index], y[split_index:]

	naive_bayes(xTrain, yTrain, xTest, yTest)
	logistic_regression(xTrain, yTrain, xTest, yTest)
	svm(xTrain, yTrain, xTest, yTest)

def svm(xTrain, yTrain, xTest, yTest):
	print('Training on SVM')

	clf = SVC(gamma='auto')
	clf.fit(xTrain, yTrain)

	print('Score is', clf.score(xTest, yTest))

def naive_bayes(xTrain, yTrain, xTest, yTest):
	print('Training on Naive Bayes')

	clf = GaussianNB()
	clf.fit(xTrain, yTrain)

	print('Score is', clf.score(xTest, yTest))

def logistic_regression(xTrain, yTrain, xTest, yTest):
	print('Training on Logistic Regression')

	clf = LogisticRegression(solver='lbfgs')
	clf.fit(xTrain, yTrain)

	predictions = np.array(clf.predict(xTest))
	np.savetxt('./output/labels.txt', predictions)

	print('Score is', clf.score(xTest, yTest))	

def extract_features(assignment_id, buckets):
	file_path = '/'.join(['./data/files/1222', assignment_id, 'Breakout.java'])
	file = util.open_file(file_path)
	features = []

	for bucket in buckets:
		features += get_features(file, bucket)

	return features

def get_features(file, bucket):
	if bucket == 'Decomposition':
		return decomposition_features(file)
	elif bucket == 'Commenting':
		return commenting_features(file)
	else:
		return []

def commenting_features(file):
	num_comments = 0
	ave_block_size = 0 
	ave_comment_length = 0

	def is_comment(line):
		return '//' in line

	def is_block_comment_start(line):
		return '/**' in line or '/*' in line

	def is_block_comment_end(line):
		return '*/' in line
	
	code_block_list = []
	curr_code_block_size = 0
	num_code_blocks = 0 
	in_comment_block_flag = 0
	num_words = 0
	for line in file:
		if is_block_comment_start(line):
			in_comment_block_flag = 1
			num_code_blocks += 1
			curr_code_block_size = 0
		elif is_block_comment_end(line):
			in_comment_block_flag = 0
			code_block_list.append(curr_code_block_size)
		elif in_comment_block_flag:
			num_words += len(line.split(' '))
			num_comments += 1
			curr_code_block_size += 1
		elif is_comment(line):
			num_words += len(line.split(' '))
			num_comments += 1
		
	if len(code_block_list) != 0:
		ave_block_size = int(sum(code_block_list) / len(code_block_list))
	else:
		ave_block_size = 0
	if num_comments != 0:
		ave_comment_length = int(num_words / num_comments)
	else:
		ave_comment_length = 0

	featureList = [num_comments, ave_block_size, ave_comment_length]
	return featureList

'''
Input: a compilable Java progrma
Output: an array containing
- # of lines in file
- # of methods in file
- average # of lines per method
'''
def decomposition_features(file):
	# TODO: check if this actually workds. It probably has some untouched edge cases,
	# but it seems to work for most cases.
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

	num_methods = len(method_line_counts)
	lines_per_method = np.mean(method_line_counts)

	return [line_count, num_methods, lines_per_method]

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

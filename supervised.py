import numpy as np
import util
import os
import subprocess
import pickle
from sklearn.naive_bayes import MultinomialNB
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.neural_network import MLPClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split

TEST_SIZE = .1

def main():
	pmd_reports = load_pkl_file('data.pkl')

	bucket = 'Decomposition'
	assignment_ids, y = util.get_data(bucket)

	X = np.array([extract_features(assignment_id, bucket, pmd_reports[assignment_id]) for assignment_id in assignment_ids])

	xTrain, xTest, yTrain, yTest = train_test_split(X, y, test_size=TEST_SIZE, stratify=y, random_state=1)

	naive_bayes(xTrain, yTrain, xTest, yTest)
	logistic_regression(xTrain, yTrain, xTest, yTest)
	gradient_boosting(xTrain, yTrain, xTest, yTest)
	mlp(xTrain, yTrain, xTest, yTest)
	random_forest(xTrain, yTrain, xTest, yTest)

	# pmd_reports = load_pkl_file('data_pmd.pkl')

	# bucket = 'Naming and Spacing'

	# assignment_ids, y = util.get_data('Naming and Spacing')

	# for a in assignment_ids:
	# 	if a not in pmd_reports:
	# 		print(a)

	# X = np.array([extract_features(assignment_id, bucket, pmd_reports[assignment_id]) for assignment_id in assignment_ids])

	# xTrain, xTest, yTrain, yTest = train_test_split(X, y, test_size=TEST_SIZE, stratify=y, random_state=0)

	# naive_bayes(xTrain, yTrain, xTest, yTest)
	# logistic_regression(xTrain, yTrain, xTest, yTest)
	# gradient_boosting(xTrain, yTrain, xTest, yTest)


def load_pkl_file(filename):
	pkl_file = open(filename, 'rb')
	pmd_reports = pickle.load(pkl_file)
	pkl_file.close()
	return pmd_reports


def train_and_test(clf, xTrain, yTrain, xTest, yTest, model_name='model_name'):
	clf.fit(xTrain, yTrain)
	print('Score on Train Set is: ', clf.score(xTrain, yTrain))
	print('Score on Test Set is: ', clf.score(xTest, yTest))

	predictions = np.array(clf.predict(xTest))

	count = 0
	for prediction, ground_truth in zip(predictions, yTest):
		# print('PREDICTION: ', prediction)
		# print('TRUTH: ', ground_truth)

		if abs(prediction - ground_truth) > 1:
			count += 1
	print('off by 2: {}'.format(count / len(predictions)))
	# np.savetxt('./output/{}_labels.txt'.format(model_name), predictions)

def random_forest(xTrain, yTrain, xTest, yTest):
	print('Training on Random Forest')
	clf = RandomForestClassifier(n_estimators=100, max_depth=2, random_state=0)
	train_and_test(clf, xTrain, yTrain, xTest, yTest, 'rf')

def mlp(xTrain, yTrain, xTest, yTest):
	print('Training on MLP')
	clf = MLPClassifier(solver='lbfgs', alpha=1e-5, hidden_layer_sizes=(4,), random_state=1)
	train_and_test(clf, xTrain, yTrain, xTest, yTest, 'mlp')


def gradient_boosting(xTrain, yTrain, xTest, yTest):
	print('Training on Gradient Boosting')
	clf = GradientBoostingClassifier(n_estimators=8, learning_rate=.1, max_depth=2, random_state=0)
	train_and_test(clf, xTrain, yTrain, xTest, yTest, 'gb')


def naive_bayes(xTrain, yTrain, xTest, yTest):
	print('Training on Naive Bayes')
	clf = MultinomialNB()
	train_and_test(clf, xTrain, yTrain, xTest, yTest, 'nb')


def logistic_regression(xTrain, yTrain, xTest, yTest):
	print('Training on Logistic Regression')
	clf = LogisticRegression(solver='lbfgs', multi_class='multinomial', max_iter=10000)
	train_and_test(clf, xTrain, yTrain, xTest, yTest, 'lr')


def extract_features(assignment_id, bucket, report=None):
	file_path = '/'.join(['./data/files', assignment_id, 'Breakout.java'])
	file = util.open_file(file_path)
	file_lines = [l for l in file]
	
	if bucket == 'Decomposition':
		return decomposition_features(file_lines, report)
	elif bucket == 'Commenting':
		return commenting_features(file)
	elif bucket == 'Naming and Spacing':
		return naming_and_spacing_features(file_lines, report)
	else:
		# TODO: implement feature extraction for other buckets.
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

'''
Input: a compilable Java progrma
Output: an array containing
- # of variables with lowerCamelCase
- # of lines with wrong indentation
'''
def naming_and_spacing_features(file, report):
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

	def get_pmd_warns(report):
		lines = report.split('\\n')
		return len(lines)

	indentation_level = 0

	wrong_camel_case_count = 0
	wrong_indentation_count = 0

	for line in file:
		var = get_variable(line)
		if var and not is_camel_case(var):
			wrong_camel_case_count += 1

		if '}' in line:
			indentation_level -= 1

		if not has_right_indentation(line, indentation_level):
			wrong_indentation_count += 1

		if '{' in line:
			indentation_level += 1

	num_pmd_warns = get_pmd_warns(report)

	return [wrong_camel_case_count, wrong_indentation_count, num_pmd_warns]

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

if __name__ == "__main__":
	main()

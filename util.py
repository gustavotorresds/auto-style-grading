import csv
import numpy as np
from collections import defaultdict

'''
Notes and observations
(1) The initial grades.csv I was working with came with extra inconvenient commas ('')
in the middle of the header. I had to erase them in order for the script to not get
messed up when splitting data by commas. My final header looks like this:
id,Decomposition,Commenting,Instance Variables and Parameters and Constants,Naming and Spacing,Logic and Redundancy
'''

VAR_TYPES = ['double', 'int', 'String', 'GLabel', 'GObject', 'GOval', 'GRect', 'void', 'boolean', 'RandomGenerator']

# The impact each label has.
# TODO: what scale should we consider?
# TODO: when label is not found (i.e., "Random Label"), the default impact
# ends up being zero. Is that what we want?
LABEL_TO_GRADE = defaultdict(int)
LABEL_TO_GRADE['Perfect'] = 7
LABEL_TO_GRADE['Minor Issues'] = 5
LABEL_TO_GRADE['Major Issues'] = 3
LABEL_TO_GRADE['Horrible'] = 1
LABEL_TO_GRADE['No comments or lots missing'] = 0

BUCKETS = ['Decomposition', 'Commenting', 'Instance Variables and Parameters and Constants',\
 'Naming and Spacing', 'Logic and Redundancy']

# The weigth each bucket should have on the grading.
# TODO: should we weight anything heavier? How should we attribute weights?
BUCKET_WEIGHT = defaultdict(int)
BUCKET_WEIGHT[BUCKETS[0]] = 1
BUCKET_WEIGHT[BUCKETS[1]] = 1
BUCKET_WEIGHT[BUCKETS[2]] = 1
BUCKET_WEIGHT[BUCKETS[3]] = 1
BUCKET_WEIGHT[BUCKETS[4]] = 1

# Threshold to classify whether a grade is good or not.
THRESHOLD = 30

BOOL_TO_LABELS = defaultdict(int)
BOOL_TO_LABELS['Good'] = ['Perfect', 'Minor Issues']
BOOL_TO_LABELS['Bad'] = ['Major Issues', 'Horrible', 'No comments or lots missing']

'''
Returns an array of 1's and 0's corresponding to each assignment.
1 indicates that the assignment had a good grade. 0 indicates the assignment
had a bad grade.
'''
def generate_labels(csv_path):
	assignment_ids, labeled_buckets = load_data(csv_path)
	processed_grades = process_grades(labeled_buckets)

	final_results = np.array([float(grade >= THRESHOLD) for grade in processed_grades])
	return final_results

'''
Returns an array of 1's and 0's corresponding to each assignment.
1 indicates that the assignment had a good grade in the specified bucket. 0 indicates
the assignment had a bad grade for the specified bucket.
'''
def generate_labels_for_bucket(csv_path, bucket):
	assignment_ids, labeled_buckets = load_data(csv_path)
	bucket_labels = labeled_buckets[:,BUCKETS.index(bucket)]

	final_results = np.array([float(label in BOOL_TO_LABELS['Good']) for label in bucket_labels])
	return final_results

'''
Returns an np array with a grade for each of the labeled buckets.
'''
def process_grades(labeled_buckets):
	grades = np.array([])

	for labels in labeled_buckets:
		grade = generate_grade(labels)
		grades = np.append(grades, grade)

	return grades

'''
Uses BUCKET_WEIGHT and LABEL_TO_GRADE constants to give the grade associated
with bucket labels for an assignment.

Input:
- np array with bucket labels.
- E.g.: ['Minor Issues', 'Perfect', 'Minor Issues', 'Minor Issues', 'Perfect']

Output:
- Integer representing grade corresponding to the bucket labels.
'''
def generate_grade(labels):
	grade = 0
	for b, bucket in enumerate(BUCKETS):
		label = labels[b]
		bucket_grade = LABEL_TO_GRADE[label]
		grade += (BUCKET_WEIGHT[bucket] * bucket_grade)
	return grade

'''
Just opens the CSV file and returns two np arrays: one for the assignment IDs and
another containing the bucket labels for each of the assignments (hence, a 2D array).
'''
def load_data(csv_path):
	with open(csv_path, 'r') as csv_fh:
		headers = csv_fh.readline().strip().split(',')

	assignment_id_col = [0]
	bucket_cols = [i for i in range(1, len(headers))]

	assignment_ids = np.loadtxt(csv_path, dtype=np.dtype(str), delimiter=',', skiprows=1, usecols=assignment_id_col)
	buckets = np.loadtxt(csv_path, dtype=np.dtype(str), delimiter=',', skiprows=1, usecols=bucket_cols)

	return assignment_ids, buckets

def open_file(path):
	try:
		file = open(path, "r")
		return file
	except IOError:
		print("Error: File does not appear to exist.")
		return None

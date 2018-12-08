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

BUCKETS = ['Decomposition', 'Commenting', 'Instance Variables and Parameters and Constants',\
 'Naming and Spacing', 'Logic and Redundancy']

GRADES = {'Major Issues': 1, 'Minor Issues': 2, 'Perfect': 3}

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

def get_data(quarter, bucket):
	csv_path = '/'.join(['.', 'data', 'grades', quarter + '.csv'])

	with open(csv_path, 'r') as csv_f:
		# headers = csv_f.readline().strip().split(',')
		reader = csv.DictReader(csv_f)

		ids = []
		labels = []

		for row in reader:
			label = row[bucket]
			if label in GRADES:
				ids.append('/'.join([quarter, row['\ufeffid']]))
				labels.append(int(GRADES[label]))

		return ids, labels

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
Just opens the CSV file and returns two np arrays: one for the assignment IDs and
another containing the bucket labels for each of the assignments (hence, a 2D array).
'''
def load_data(csv_path):
	with open(csv_path, 'r') as csv_fh:
		headers = csv_fh.readline().strip().split(',')

	assignment_id_col = [0]
	bucket_cols = [i for i in range(1, len(headers))]

	assignment_ids = np.loadtxt(csv_path, dtype=np.dtype(str), delimiter=',', skiprows=1, usecols=assignment_id_col)
	complete_ids = np.array([csv_path + assignment_id for assignment_id in assignment_ids])

	buckets = np.loadtxt(csv_path, dtype=np.dtype(str), delimiter=',', skiprows=1, usecols=bucket_cols)

	return complete_ids, buckets

def open_file(path):
	try:
		file = open(path, "r")
		return file
	except IOError:
		print("Error: File does not appear to exist.")
		return None

import numpy as np
import util

def main():
	labels = util.generate_labels('./data/grades.csv')

	# Do something with grades, e.g., save as .csv
	np.savetxt('./output/labels.txt', labels)

def svm():
	pass

def naive_bayes():
	pass

if __name__ == "__main__":
	main()

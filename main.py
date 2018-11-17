import numpy as np
import util

def main():
	grades = util.process_grades('./data/grades.csv')

	# Do something with grades, e.g., save as .csv
	np.savetxt('./output/processed-grades.csv', grades)

def svm():
	pass

def naive_bayes():
	pass

if __name__ == "__main__":
	main()

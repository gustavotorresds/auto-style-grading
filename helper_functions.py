import os

# Just a helper to see which directories didn't have a "Breakout.java" file.

root = './data/files/1363'

f = []
for (dirpath, dirnames, filenames) in os.walk(root):
	f.extend(dirnames)
	break

for submission in f:
	l = os.listdir(root + '/' + submission)
	if 'Breakout.java' not in l:
		print(submission)
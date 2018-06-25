package a10;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Implements a kNN classifer on Fruit samples.
 * @author dejohnso
 *
 */
public class KNNClassifier {

	private int k; // The number of nearest neighbors used
	private List<Face> samples; // The List of known samples
	private List<Face> kClosestSet; // The sublist of the nearest k samples
	private Face unknown;

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public List<Face> getSamples() {
		return samples;
	}

	public void setSamples(List<Face> samples) {
		this.samples = samples;
	}

	public List<Face> getkClosestSet() {
		return kClosestSet;
	}

	public void setkClosestSet(List<Face> kClosestSet) {
		this.kClosestSet = kClosestSet;
	}

	public Face getUnknown() {
		return unknown;
	}

	/**
	 * Swap out the old unknown for the new one.
	 * @param newUnknown
	 */
	public void setUnknown(Face newUnknown) {
		// To test this, pull one sample out of the known fruit.
		// Pretend we do not know its classification and classify it.
		// Compare the kNN classification with what it really is.
		// If there already was an unknown, add it back into samples
		if (unknown != null) 
			samples.add(unknown);
		
		// Now find the unknown, save it, and remove it from the samples
		int unknownIndex = -1;
		for (int index = 0; index < samples.size(); index++) {
			if (samples.get(index) == newUnknown)
				unknownIndex = index;
		}
		if (unknownIndex >= 0) {
			unknown = samples.get(unknownIndex);
			samples.remove(unknownIndex);
		}
		kClosestSet = null;
	}


	/**
	 * Construct the classifier object with a k value set.
	 * @param kInit
	 */
	public KNNClassifier(int kInit) {
		k = kInit; 
		samples = new ArrayList<Face>(); // Make the List to store samples in
		kClosestSet = null; // The nearest set is not yet known
		unknown = null;
	}
		
	
	/**
	 * Add a known sample to the classifier.
	 * @param sample
	 */
	public void addSample(Face sample) {
		samples.add(sample);
	}

	
	/**
	 * Get rid of the existing samples in preparation for adding new ones.
	 */
	public void clearSamples() {
		samples.clear();
	}

	
	/**
	 * Find the distance from every known sample to the unknown
	 * and store that distance on each known sample.
	 * @param unknown
	 */
	private void computeDistanceFromUnknownToSamples(Face unknown) {
		for (Face s : samples)
			s.calculateDistance(unknown);
	}
	
	/**
	 * Set the internal kClosestSet to the nearest k samples. Must call
	 * the computeDistance method first. 
	 */
	private void findKClosest() {
		kClosestSet = new ArrayList<Face>(); // Implement and change.
		for (int i = 0; i < k; i++) {
			kClosestSet.add(samples.get(i));
		}
		for(int i = k; i < samples.size(); i++) {
			Collections.sort(kClosestSet); //Ascending order (low to high)
			if(samples.get(i).distance < kClosestSet.get(k -1).distance) {
				kClosestSet.remove(k -1);
				kClosestSet.add(samples.get(i));
			}
		}
	}
	
	
	/**
	 * Use the calculated kClosestSet to vote on the classification.
	 * This method uses a Map to store classifications and their vote count,
	 * then searches for a maximum vote count. It returns the classification 
	 * with the highest vote.
	 * @return the String classification.
	 */
	private String voteOnClassification() {
		Map<String, Integer> votes = new HashMap<String, Integer>();
		for(Face sample: kClosestSet) {
			String classification = sample.getClassification();
			if(votes.containsKey(classification)) {
				votes.put(classification, votes.get(classification) + 1);
			}
			else {
				votes.put(classification, 1);
			}
		}
		
		String decision = "";
		int decisionVotes = 0;
		for(Entry<String, Integer> e : votes.entrySet()) {
			if(e.getValue() > decisionVotes) {
				decisionVotes = e.getValue();
				decision = e.getKey();
			}
		}
		
		return decision;
	}
	

	
	/**
	 * The main method to find a classification. The code
	 * calls methods to enact each of the needed steps.
	 * @param unknown sample
	 * @return the classification
	 */
	public String classifyUnknownSample(Face unknown) {
		computeDistanceFromUnknownToSamples(unknown);
		findKClosest();
		String classification = voteOnClassification();
		return classification;
	}
			
	public void pickUnknown() {
		// To test this, pull one sample out of the known fruit.
		// Pretend we do not know its classification and classify it.
		// Compare the kNN classification with what it really is.
		
		// If there already was an unknown, add it back into samples
		if (unknown != null) 
			samples.add(unknown);
		
		// Now find the unknown, save it, and remove it from the samples
		Random rand = new Random();
		int randomIndex = rand.nextInt(samples.size());
		unknown = samples.get(randomIndex);
		samples.remove(randomIndex);
	}
	
	/**
	 * Test code to drive the classifier. It loads samples from a file
	 * and pretends one sample is unknown. 
	 * The classifier then runs and compares its answer with the known value.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Make a classifier
		KNNClassifier faceClassifier = new KNNClassifier(5);
		// Get the data
		ArrayList<Face> faces = Face.readSamples(new File("faces"));

		// Add each sample to the classifier
		for (Face f : faces)
			faceClassifier.addSample(f);
		faceClassifier.pickUnknown();
		String result = faceClassifier.classifyUnknownSample(faceClassifier.unknown);
		System.out.println("The k nearest neighbors are:");
		for (Face f : faceClassifier.kClosestSet)
			System.out.println(f);
		System.out.println("The majority vote is for: " + result);
		System.out.println("The real classification is: " + faceClassifier.unknown.getClassification());
	}
}

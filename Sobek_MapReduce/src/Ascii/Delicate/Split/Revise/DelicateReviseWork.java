package Ascii.Delicate.Split.Revise;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiMerge;
import usualTool.AtFileWriter;
import GlobalProperty.GlobalProperty;
import SOBEK.Runtimes;
import SOBEK.SobekDem;

public class DelicateReviseWork {
	// merge ascii
	private AsciiBasicControl mergeAscii;
	private AsciiBasicControl mergeAsciiKn;
//	private Path2D mergedPath;

	// asciiFile
	// <===================================>
	private AsciiBasicControl declineAscii;
	private AsciiBasicControl declineKn;
	private double declineSpendTime = 0;

	private AsciiBasicControl extendAscii;
	private AsciiBasicControl extendKn;
	private double extendSpendTime = 0;
	// <===================================>

	// use for revise delicate asciiFile
	// <===================================>

	// the cut line coefficient
	private double cutLineXcoefficient = 0;
	private double cutLineYcoefficient = 0;
	private double cutLineIntercept = 0;

	//
	private double moveLineCenterX = 0;
	private double moveLineCenterY = 0;
	private double boundaryExtendX = 0;
	private double boundaryExtendY = 0;
	private double boundaryDeclineX = 0;
	private double boundaryDeclineY = 0;

	// <====================================>

	public DelicateReviseWork(AsciiBasicControl declineAscii, AsciiBasicControl extendAscii) throws IOException {
		this.declineAscii = declineAscii;
		this.extendAscii = extendAscii;

		// get the merged asciiFile
		// then fill the null grid by the original asciiFile
		this.mergeAscii = new AsciiMerge(this.extendAscii, this.declineAscii).getMergedAscii();
		this.mergeAscii = new AsciiBasicControl(GlobalProperty.originalDelicate).getIntersectAscii(this.mergeAscii);
		this.mergeAsciiKn = new AsciiBasicControl(GlobalProperty.originalDelicateKn)
				.getClipAsciiFile(this.mergeAscii.getBoundary());

		getIntersectProerpty();
	}

	public DelicateReviseWork(String declineAsciiAdd, String extendAsciiAdd) throws IOException {
		this.declineAscii = new AsciiBasicControl(declineAsciiAdd);
		this.extendAscii = new AsciiBasicControl(extendAsciiAdd);

		// get the merged asciiFile
		// then fill the null grid by the original asciiFile
		this.mergeAscii = new AsciiMerge(this.extendAscii, this.declineAscii).getMergedAscii();
		this.mergeAscii = new AsciiBasicControl(GlobalProperty.originalDelicate).getIntersectAscii(this.mergeAscii);
		this.mergeAsciiKn = new AsciiBasicControl(GlobalProperty.originalDelicateKn)
				.getClipAsciiFile(this.mergeAscii.getBoundary());

		getIntersectProerpty();
	}
	// <==========================================>

	/*
	 * 
	 */
	// <==========================================>
	// <for public function>
	// <==========================================>
	public AsciiBasicControl getExtendAscii() {
		return this.extendAscii;
	}

	public double getExtendSpendTime() {
		return this.extendSpendTime;
	}

	public AsciiBasicControl getDeclineAscii() {
		return this.declineAscii;
	}

	public double getDeclineSpendTime() {
		return this.declineSpendTime;
	}
	// <==========================================>

	/*
	 * intersection line movement
	 * 
	 */
	// <==========================================>
	public void startRevising(int maxTimes) throws IOException, InterruptedException {
		// split asciiFile to two part ,by the cut line
		clipAscii();

		// judge for the spend time
		spendTimeJudgment(maxTimes);
	}

	private void spendTimeJudgment(int times) throws IOException, InterruptedException {
		double differX;
		double differY;

		Boolean judgement = true;
		for (int index = 0; index < times && judgement; index++) {
			System.out.println("Revise times : " + (index + 1));
			this.declineSpendTime = sobekRuntimes(this.declineAscii, this.declineKn);
			System.out.println("decline spend time : " + this.declineSpendTime);
			this.extendSpendTime = sobekRuntimes(this.extendAscii, this.extendKn);
			System.out.println("extend spend time : " + this.extendSpendTime);

			if (declineSpendTime > GlobalProperty.splitTime || extendSpendTime > GlobalProperty.splitTime) {
				// move to decline side
				if (declineSpendTime > GlobalProperty.splitTime) {
					this.boundaryExtendX = this.moveLineCenterX;
					this.boundaryExtendY = this.moveLineCenterY;

					this.moveLineCenterX = (this.boundaryDeclineX + this.boundaryExtendX) / 2;
					this.moveLineCenterY = (this.boundaryDeclineY + this.boundaryExtendY) / 2;

					differX = this.moveLineCenterX - this.boundaryExtendX;
					differY = this.moveLineCenterY - this.boundaryExtendY;
					this.cutLineIntercept = this.cutLineIntercept - this.cutLineXcoefficient * differX
							- this.cutLineYcoefficient * differY;

				}
				// move to extend side
				else if (extendSpendTime > GlobalProperty.splitTime) {
					this.boundaryExtendX = this.moveLineCenterX;
					this.boundaryExtendY = this.moveLineCenterY;

					this.moveLineCenterX = (this.boundaryDeclineX + this.boundaryExtendX) / 2;
					this.moveLineCenterY = (this.boundaryDeclineY + this.boundaryExtendY) / 2;

					differX = this.moveLineCenterX - this.boundaryDeclineX;
					differY = this.moveLineCenterY - this.boundaryDeclineY;
					this.cutLineIntercept = this.cutLineIntercept - this.cutLineXcoefficient * differX
							- this.cutLineYcoefficient * differY;
				}

				// clip a new asciiFile
				clipAscii();
			} else {
				judgement = false;
			}
		}
	}
	// <=====================================================>

	/*
	 * 
	 */
	// <=====================================================>
	// < revise demBoundary by rough demFile>
	// <=====================================================>
	private Map<String, Double> reviseBoundary(Map<String, Double> boundary) throws IOException {
		AsciiBasicControl roughAscii = new AsciiBasicControl(GlobalProperty.originalRough);
		boundary = roughAscii.getIntersectBoundary(boundary);

		double roughCellSize = Double.parseDouble(roughAscii.getProperty().get("cellSize"));
		double delicateCellSize = Double.parseDouble(this.mergeAscii.getProperty().get("cellSize"));

		boundary.put("minX", boundary.get("minX") - roughCellSize - delicateCellSize);
		boundary.put("maxX", boundary.get("maxX") + roughCellSize + delicateCellSize);
		boundary.put("minY", boundary.get("minY") - roughCellSize - delicateCellSize);
		boundary.put("maxY", boundary.get("maxY") + roughCellSize + delicateCellSize);

		return boundary;
	}

	/*
	 * 
	 */
//<=======================================================>
	private void clipAscii() throws IOException {
		List<Map<String, Double>> sidePoints = this.mergeAscii.getIntersectSideBoundary(this.cutLineXcoefficient,
				this.cutLineYcoefficient, this.cutLineIntercept);

		for (int index = 0; index < sidePoints.size(); index++) {
			// to pick up which asciiFile is contained the center of boundary
			Map<String, Double> temptPoints = sidePoints.get(index);
			double temptCenterX = (temptPoints.get("minX") + temptPoints.get("maxX")) / 2;
			double temptCenterY = (temptPoints.get("minY") + temptPoints.get("maxY")) / 2;

			// revise clip boundary by roughBoundary
			temptPoints = reviseBoundary(temptPoints);

			// clipAscii
			if (this.declineAscii.isContain(temptCenterX, temptCenterY)) {
				this.declineAscii = this.mergeAscii.getClipAsciiFile(temptPoints);
				this.declineKn = this.mergeAsciiKn.getClipAsciiFile(temptPoints);
			} else if (this.extendAscii.isContain(temptCenterX, temptCenterY)) {
				this.extendAscii = this.mergeAscii.getClipAsciiFile(temptPoints);
				this.extendKn = this.mergeAsciiKn.getClipAsciiFile(temptPoints);
			} else {
				System.out.println(" cutLine error while clip asciiFile");
			}
			
		}
	}

	// <==========================================>

	/*
	 * 
	 * 
	 */
	// <==========================================>
	// <get center of ascii>
	// <==========================================>
	private void getIntersectProerpty() {
		double[] declineAsciiCneter = this.declineAscii.getCoordinate(
				Integer.parseInt(this.declineAscii.getProperty().get("column")) / 2,
				Integer.parseInt(this.declineAscii.getProperty().get("row")) / 2);
		this.boundaryDeclineX = declineAsciiCneter[0];
		this.boundaryDeclineY = declineAsciiCneter[1];

		// set the extendBoundary to the center of the intersect
		Map<String, Double> intersectBoundary = this.declineAscii.getIntersectBoundary(this.extendAscii);
		this.boundaryExtendX = (intersectBoundary.get("minX") + intersectBoundary.get("maxX")) / 2;
		this.boundaryExtendY = (intersectBoundary.get("minY") + intersectBoundary.get("maxY")) / 2;

		// determine the cut line by these two asciiFiles
		double differY = (boundaryDeclineY - boundaryExtendY);
		double differX = (boundaryDeclineX - boundaryExtendX);

		// the center point of current cutLine
		this.moveLineCenterX = (this.boundaryExtendX + this.boundaryDeclineX) / 2;
		this.moveLineCenterY = (this.boundaryExtendY + this.boundaryDeclineY) / 2;

		// vertical cut line
		if (differY == 0) {
			this.cutLineXcoefficient = 1;
			this.cutLineYcoefficient = 0;
			this.cutLineIntercept = -1 * this.moveLineCenterX;

			// horizontal cut line
		} else if (differX == 0) {
			this.cutLineXcoefficient = 0;
			this.cutLineYcoefficient = 1;
			this.cutLineIntercept = -1 * this.moveLineCenterY;

			// normal the slope of the cut line
			// will change it in to the vertical or horizontal cutLine
		} else {
			this.cutLineXcoefficient = (differX / differY);
			if (this.cutLineXcoefficient >= 1) {
				this.cutLineXcoefficient = 1;
				this.cutLineYcoefficient = 0;
			} else {
				this.cutLineXcoefficient = 0;
				this.cutLineYcoefficient = 1;
			}
			this.cutLineIntercept = -1 * (this.cutLineYcoefficient * this.moveLineCenterY
					+ this.cutLineXcoefficient * this.moveLineCenterX);
		}
	}

	/*
	 */
	// <========================================>
	// <Running SobekModel>
	// <========================================>
	private double sobekRuntimes(AsciiBasicControl ascii, AsciiBasicControl kn)
			throws IOException, InterruptedException {
		String temptSaveAscii = GlobalProperty.saveFolder_tempt + GlobalProperty.saveFile_DelicateDem;
		String temptSaveKn = GlobalProperty.saveFolder_tempt + GlobalProperty.saveFile_DelicateDemKn;

		new AtFileWriter(ascii.getAsciiFile(), temptSaveAscii).textWriter(" ");
		new AtFileWriter(kn.getAsciiFile(), temptSaveKn).textWriter(" ");

		SobekDem sobekDem = new SobekDem();
		sobekDem.addDelicateDem(temptSaveAscii, temptSaveKn);
		sobekDem.start();
		if (GlobalProperty.nodeFunction_DelicateTotal) {
			sobekDem.setDelicateNode();
		}

		Runtimes sobekRuntimes = new Runtimes();
		sobekRuntimes.RuntimesSetLimit();
		return sobekRuntimes.getSimulateTime();
	}

}

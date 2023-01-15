package spoon;

import spoon.reflect.CtModel;

public class SpoonParser {
	private String projectPath;

	public SpoonParser(String projectPath) {
		super();
		this.projectPath = projectPath;
	}

	public CtModel getModel() {
		Launcher launcher = new Launcher();
		launcher.addInputResource(projectPath);
		launcher.buildModel();
		return launcher.getModel();
	}

}

package analysis.exercise1;

import java.util.stream.Stream;

import analysis.CallGraph;
import analysis.CallGraphAlgorithm;
import soot.Body;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public class CHAAlgorithm extends CallGraphAlgorithm {

	@Override
	protected String getAlgorithm() {
		return "CHA";//"master l "
	}

	@Override
	protected void populateCallGraph(Scene scene, CallGraph cg) {
		// Your implementation goes here, also feel free to add methods as needed
		// To get your entry points we prepared getEntryPoints(scene) in the superclass
		// for you
		Stream<SootMethod> entryPoints = getEntryPoints(scene);
		entryPoints.forEach(entryPoint -> {
			System.out.println(entryPoint);
			if (entryPoint.toString().equals("<target.exercise1.SimpleExample: void main(java.lang.String[])>")) {
				checkMethod(null, entryPoint, cg, scene);
			}
		});
		
	}

	private void checkMethod(SootMethod src, SootMethod entryPoint, CallGraph cg, Scene scene) {
		// TODO Auto-generated method stub
		try {

			if (!cg.hasNode(entryPoint)) {
				cg.addNode(entryPoint);
				// System.out.println("added node: " + entryPoint);
			}

			if (src != null && !cg.hasEdge(src, entryPoint)) {
				cg.addEdge(src, entryPoint);
				 System.out.println(src + " -> " + entryPoint);
			}

			if (entryPoint.hasActiveBody()) {
				Body b = entryPoint.getActiveBody();
				for (Unit u : b.getUnits()) {

					Stmt s = (Stmt) u;
					if (s.containsInvokeExpr()) {

						InvokeExpr expr = s.getInvokeExpr();
						checkMethod(entryPoint, expr.getMethod(), cg, scene);
					}

				}

			} else if (entryPoint.isAbstract()) {
				scene.getApplicationClasses().forEach(sampleClass -> {
					sampleClass.getInterfaces().forEach(implementedInterface -> {
						if (implementedInterface == entryPoint.getDeclaringClass()) {
							sampleClass.getMethods().forEach(method -> {
								if (method.getSubSignature().equals(entryPoint.getSubSignature())) {
									if (!cg.hasNode(method)) {
										cg.addNode(method);
									}

									if (!cg.hasEdge(src, method)) {
										cg.addEdge(src, method);
									}

									checkMethod(entryPoint, method, cg, scene);
								}
							});
						}
					});
				});

			}

		} catch (Exception e) {
			System.out.println("EXCEPTION:");
		} finally {
		}
	}

}

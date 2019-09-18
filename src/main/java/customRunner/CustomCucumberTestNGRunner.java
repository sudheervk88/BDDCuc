package customRunner;

import cucumber.api.testng.*;
import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.CucumberFeature;
import gherkin.formatter.Formatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCucumberTestNGRunner {

    private Runtime runtime;
    private RuntimeOptions runtimeOptions;
    private ResourceLoader resourceLoader;
    private FeatureResultListener resultListener;
    private ClassLoader classLoader;

    /**
     * Bootstrap the cucumber runtime
     *
     * @param clazz Which has the cucumber.api.CucumberOptions and org.testng.annotations.Test annotations
     */
    public CustomCucumberTestNGRunner(Class clazz) {
        classLoader = clazz.getClassLoader();
        resourceLoader = new MultiLoader(classLoader);

        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
        runtimeOptions = runtimeOptionsFactory.create();

        TestNgReporter reporter = new TestNgReporter(System.out);
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        resultListener = new FeatureResultListener(runtimeOptions.reporter(classLoader), runtimeOptions.isStrict());
        runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
    }

    /**
     * Run the Cucumber features
     */
    public void runCukes() {
        for (CucumberFeature cucumberFeature : getFeatures()) {
            cucumberFeature.run(
                    runtimeOptions.formatter(classLoader),
                    resultListener,
                    runtime);
        }
        finish();
        if (!resultListener.isPassed()) {
            throw new CucumberException(resultListener.getFirstError());
        }
    }

    public void runCucumber(CucumberFeature cucumberFeature) {
        resultListener.startFeature();
        cucumberFeature.run(
                runtimeOptions.formatter(classLoader),
                resultListener,
                runtime);

        if (!resultListener.isPassed()) {
            throw new CucumberException(resultListener.getFirstError());
        }
    }

    public void finish() {
        Formatter formatter = runtimeOptions.formatter(classLoader);

        formatter.done();
        formatter.close();
        runtime.printSummary();
    }

    /**
     * @return List of detected cucumber features
     */
    public List<CucumberFeature> getFeatures() {
        List<CucumberFeature> modifiedList = getModifiedFeatureList();

       /* for (CucumberFeature cucumberFeature: featureList) {
         if(value.equalsIgnoreCase(cucumberFeature.getGherkinFeature().getName())){
             System.out.println("Found feature file successfully");
             modifiedList.add(cucumberFeature);
         }
    } */



        return modifiedList;
    }

    private List<CucumberFeature> getModifiedFeatureList() {
        String[] value = System.getProperty("FeatureName").split(",");
        List<CucumberFeature> featureList = runtimeOptions.cucumberFeatures(resourceLoader);
        List<CucumberFeature> modifiedList = new ArrayList<>();

        Map<String,CucumberFeature> featureMap = getFeatureMap(featureList);
        for (int i=0;i<value.length;i++){
            modifiedList.add(featureMap.get(value[i]));

        }
        return modifiedList;
    }

    private Map<String, CucumberFeature> getFeatureMap(List<CucumberFeature> featureList) {
         Map<String,CucumberFeature> featureMap = new HashMap<>();
        for (CucumberFeature cucumberFeature: featureList) {
            featureMap.put(cucumberFeature.getGherkinFeature().getName(),cucumberFeature);
        }
      return featureMap;
    }

    /**
     * @return returns the cucumber features as a two dimensional array of
     * {@link CucumberFeatureWrapper} objects.
     */
    public Object[][] provideFeatures() {
        try {
            List<CucumberFeature> features = getFeatures();
            List<Object[]> featuresList = new ArrayList<Object[]>(features.size());
            for (CucumberFeature feature : features) {
                featuresList.add(new Object[]{new CucumberFeatureWrapperImpl(feature)});
            }
            return featuresList.toArray(new Object[][]{});
        } catch (CucumberException e) {
            return new Object[][]{new Object[]{new CucumberExceptionWrapper(e)}};
        }
    }




}

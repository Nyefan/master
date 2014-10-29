package org.neuroph.samples.convolution;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.neuroph.contrib.evaluation.NeuralNetworkEvaluationService;
import org.neuroph.contrib.learning.CrossEntropyError;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.error.MeanSquaredError;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.comp.layer.ConvolutionalLayer;
import org.neuroph.nnet.ConvolutionalNetwork;
import org.neuroph.nnet.comp.ConvolutionalUtils;
import org.neuroph.nnet.comp.layer.FeatureMapsLayer;
import org.neuroph.nnet.comp.layer.InputMapsLayer;
import org.neuroph.nnet.comp.Kernel;
import org.neuroph.nnet.comp.layer.Layer2D;
import org.neuroph.nnet.comp.layer.PoolingLayer;
import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.input.WeightedSum;
import org.neuroph.nnet.comp.neuron.BiasNeuron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.ConvolutionalBackpropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.samples.convolution.mnist.MNISTDataSet;
import org.neuroph.samples.convolution.util.WeightVisualiser;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.NeuronProperties;
import org.neuroph.util.TransferFunctionType;

/**
 * Konvolucioni parametri
 * <p/>
 * Globalna arhitektura: Konvolucioni i pooling lejeri - naizmenicno (samo konvolucioni ili naizmenicno konvolccioni pooling)
 * Za svaki lajer da ima svoj kernel (mogu svi konvolucioni da imaju isti kernel, ili svi pooling isti kernel)
 * da mogu da zadaju neuron properties (transfer funkciju) za konvolucioni i pooling lejer(input)
 * Konektovanje lejera? - po defaultu full connect (ostaviti api za custom konekcije)
 * <p/>
 * addFeatureMaps...
 * connectFeatureMaps
 * <p/>
 * Helper utility klasa...
 * <p/>
 * Osnovni kriterijumi:
 * 1. Jednostavno kreiranje default neuronske mreze
 * 2. Laka customizacija i kreiranje custom arhitektura: konvolucionih i pooling lejera i transfer/input funkcija
 * Napraviti prvo API i ond aprilagodti kod
 * <p/>
 * ------------------------
 * <p/>
 * promeniti nacin kreiranja i dodavanja feature maps layera
 * resiti InputMaps Layer, overridovana metoda koja baca unsupported exception ukazuje da nesto nije u redu sa dizajnom
 * Da li imamo potrebe za klasom kernel  - to je isto kao i dimension?
 * <p/>
 * zasto je public abstract void connectMaps apstraktna? (u klasi FeatureMapsLayer)
 * <p/>
 * InputMapsLayer konstruktoru superklase prosledjuje null...
 * <p/>
 * fullConectMapLayers
 *
 * @author zoran
 */


public class MNISTBenchmarkSample {

    static class LearningListener implements LearningEventListener {


        long start = System.currentTimeMillis();

        public void handleLearningEvent(LearningEvent event) {
            BackPropagation bp = (BackPropagation) event.getSource();
            System.out.println("Current iteration: " + bp.getCurrentIteration());
            System.out.println("Error: " + bp.getTotalNetworkError());
            System.out.println((System.currentTimeMillis() - start) / 1000.0);
            start = System.currentTimeMillis();
        }

    }


    public static void main(String[] args) {
        try {

            DataSet trainSet = MNISTDataSet.createFromFile(MNISTDataSet.TRAIN_LABEL_NAME, MNISTDataSet.TRAIN_IMAGE_NAME, 60000);
            DataSet testSet = MNISTDataSet.createFromFile(MNISTDataSet.TEST_LABEL_NAME, MNISTDataSet.TEST_IMAGE_NAME, 10000);


//            MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(784, 300, 10);
//
            LearningListener listener = new LearningListener();
//            neuralNet.getLearningRule().addListener(listener);
//
//            neuralNet.getLearningRule().setMaxError(0.1);
//            neuralNet.getLearningRule().setMaxIterations(24);
//            neuralNet.getLearningRule().setLearningRate(0.01);
//
//            double average = 0;



//            neuralNet.save("/mlp.nnet");

//
//            // create convolutional neural network
//            ConvolutionalNetwork convolutionalNet = new ConvolutionalNetwork();
//
//            Layer2D.Dimensions inputMapSize = new Layer2D.Dimensions(28, 28);
//            Kernel convolutionKernel = new Kernel(5, 5);
//            Kernel poolingKernel = new Kernel(2, 2);
//
//            InputMapsLayer inputLayer = new InputMapsLayer(inputMapSize, 1);
//            inputLayer.setLabel("Input Layer");
//            // just add number of maps to this constructor, and provide constructors with neuronProperties
//            ConvolutionalLayer convolutionLayer1 = new ConvolutionalLayer(inputLayer, convolutionKernel, 8);
//            convolutionLayer1.setLabel("Convolution 1");
//            PoolingLayer poolingLayer1 = new PoolingLayer(convolutionLayer1, poolingKernel);
//            poolingLayer1.setLabel("Pool 1");
//
//
//            convolutionalNet.addLayer(inputLayer);
//            convolutionalNet.addLayer(convolutionLayer1);
//            ConvolutionalUtils.fullConectMapLayers(inputLayer, convolutionLayer1);
//
//            ConvolutionalUtils.fullConectMapLayers(convolutionLayer1, poolingLayer1);
//            convolutionalNet.addLayer(poolingLayer1);
//
//            ConvolutionalLayer convolutionLayer2 = new ConvolutionalLayer(poolingLayer1, convolutionKernel, 16);
//            convolutionLayer2.setLabel("Convolution 2");
//            convolutionalNet.addLayer(convolutionLayer2);
//            ConvolutionalUtils.fullConectMapLayers(poolingLayer1, convolutionLayer2);
//
//
//            PoolingLayer poolingLayer2 = new PoolingLayer(convolutionLayer2, poolingKernel);
//            poolingLayer2.setLabel("Pool 2");
//            convolutionalNet.addLayer(poolingLayer2);
//            ConvolutionalUtils.fullConectMapLayers(convolutionLayer2, poolingLayer2);
//
//            ConvolutionalLayer convolutionLayer3 = new ConvolutionalLayer(poolingLayer2, new Kernel(4, 4), 120);
//            convolutionLayer3.setLabel("Convolution 3");
//            convolutionalNet.addLayer(convolutionLayer3);
//            ConvolutionalUtils.fullConectMapLayers(poolingLayer2, convolutionLayer3);
//
//            NeuronProperties neuronProperties = new NeuronProperties();
//            neuronProperties.setProperty("useBias", true);
//            neuronProperties.setProperty("transferFunction", TransferFunctionType.SIGMOID);
//            neuronProperties.setProperty("inputFunction", WeightedSum.class);
////
////            Layer preFinal = new Layer(86, neuronProperties);
////            convolutionalNet.addLayer(preFinal);
//
//            Layer outputLayer = new Layer(10, neuronProperties);
//            convolutionalNet.addLayer(outputLayer);
////            ConnectionFactory.fullConnect(preFinal,outputLayer);
//            fullConnect(convolutionLayer3, outputLayer, true);
//
//            // this should be set by default
//            convolutionalNet.setInputNeurons(inputLayer.getNeurons());
//            convolutionalNet.setOutputNeurons(outputLayer.getNeurons());
//



            NeuralNetwork<BackPropagation> convolutionalNet = ConvolutionalNetwork.load("/fuluNET1.nnet");
            NeuralNetworkEvaluationService.completeEvaluation(convolutionalNet, testSet);


            convolutionalNet.setLearningRule(new MomentumBackpropagation());
            convolutionalNet.getLearningRule().setLearningRate(0.0003);
            convolutionalNet.getLearningRule().setMaxError(0.00001);
            convolutionalNet.getLearningRule().setMaxIterations(10);
            convolutionalNet.getLearningRule().setErrorFunction(new MeanSquaredError());

            // create and set learning listener
            listener = new LearningListener();
            convolutionalNet.getLearningRule().addListener(listener);



            convolutionalNet.learn(trainSet);

            NeuralNetworkEvaluationService.completeEvaluation(convolutionalNet, testSet);


            convolutionalNet.save("/fuluNET2.nnet");


//            WeightVisualiser visualiser1 = new WeightVisualiser(convolutionLayer1.getFeatureMap(0), convolutionKernel);
//            visualiser1.displayWeights();
//            WeightVisualiser visualiser2 = new WeightVisualiser(convolutionLayer1.getFeatureMap(1), convolutionKernel);
//            visualiser2.displayWeights();
//            WeightVisualiser visualiser3 = new WeightVisualiser(convolutionLayer1.getFeatureMap(2), convolutionKernel);
//            visualiser3.displayWeights();
//            WeightVisualiser visualiser4 = new WeightVisualiser(convolutionLayer1.getFeatureMap(3), convolutionKernel);
//            visualiser4.displayWeights();
//            WeightVisualiser visualiser5 = new WeightVisualiser(convolutionLayer1.getFeatureMap(4), convolutionKernel);
//            visualiser5.displayWeights();
//            WeightVisualiser visualiser6 = new WeightVisualiser(convolutionLayer1.getFeatureMap(5), convolutionKernel);
//            visualiser6.displayWeights();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveImage(Layer2D outMap) throws IOException {
        BufferedImage finalImage = new BufferedImage(outMap.getWidth(), outMap.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        int[] rgbData = new int[outMap.getWidth() * outMap.getHeight()];
        for (int y = 0; y < outMap.getHeight(); y++) {
            for (int x = 0; x < outMap.getWidth(); x++) {
                int val = (int) (outMap.getNeuronAt(x, y).getOutput() * 255);
                rgbData[y * (outMap.getWidth()) + x] = new Color(val, val, val).getRGB();
            }
        }

        finalImage.setRGB(0, 0, outMap.getWidth(), outMap.getHeight(), rgbData, 0, outMap.getWidth());
        File f = new File("E:\\Coursera\\Images\\CNN" + (int) (Math.random() * 1000) + ".bmp");
        ImageIO.write(finalImage, "bmp", f);

    }

    public static void test(ConvolutionalNetwork cnn, DataSet testSet) {
        double sum = 0;
        for (DataSetRow testSetRow : testSet.getRows()) {
            cnn.setInput(testSetRow.getInput());
            cnn.calculate();

            double[] networkOutput = cnn.getOutput();

            System.out.println("Desired: " + Arrays.toString(testSetRow.getDesiredOutput()));
            System.out.println("Actual : " + Arrays.toString(networkOutput));

            for (int i = 0; i < networkOutput.length; i++) {
                sum += Math.pow(testSetRow.getDesiredOutput()[i] - networkOutput[i], 2) * 0.5;
            }
        }
        System.out.println(sum / testSet.getRows().size());

    }



    public static void fullConnect(FeatureMapsLayer fromLayer, Layer toLayer, boolean connectBiasNeuron) {
        for (Neuron fromNeuron : fromLayer.getNeurons()) {
            if (fromNeuron instanceof BiasNeuron) {
                continue;
            }
            for (Neuron toNeuron : toLayer.getNeurons()) {
                ConnectionFactory.createConnection(fromNeuron, toNeuron);
            }
        }
    }
}

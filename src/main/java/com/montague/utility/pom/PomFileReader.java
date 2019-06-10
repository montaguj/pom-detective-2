package com.montague.utility.pom;

import com.montague.utility.exception.PomReaderException;
import org.apache.maven.pom._4_0.Model;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;

public class PomFileReader implements PomReader {

    @Override
    public Model getModelFromFile(String path) {
        try {
            JAXBContext context = JAXBContext.newInstance(Model.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            FileInputStream fis = new FileInputStream(new File(path));
            JAXBElement<Model> modelJAXBElement = unmarshaller.unmarshal(new StreamSource(fis), Model.class);
            return modelJAXBElement.getValue();
        } catch (Exception e) {
            throw new PomReaderException("could not read pom at path: " + path);
        }
    }
}

package org.protege.owl.server.changes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import org.protege.owl.server.api.ChangeDocument;
import org.protege.owl.server.api.ChangeMetaData;
import org.protege.owl.server.api.DocumentFactory;
import org.protege.owl.server.api.OntologyDocumentRevision;
import org.semanticweb.owlapi.model.OWLOntologyChange;

public class ChangeDocumentUtilities {
	
	public static void writeEmptyChanges(DocumentFactory factory, File historyFile) throws IOException {
		ChangeDocument changes = factory.createChangeDocument(new ArrayList<OWLOntologyChange>(), new TreeMap<OntologyDocumentRevision, ChangeMetaData>(), OntologyDocumentRevision.START_REVISION);
		ChangeDocumentUtilities.writeChanges(changes, historyFile);
	}


	public static void writeChanges(ChangeDocument changes, File historyFile) throws IOException {
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(historyFile));
		try {
			changes.writeChangeDocument(fos);
		}
		finally {
			fos.flush();
			fos.close();
		}
	}
	
	public static ChangeDocument readChanges(DocumentFactory factory, File historyFile, OntologyDocumentRevision start, OntologyDocumentRevision end) throws IOException {
		InputStream fis = new BufferedInputStream(new FileInputStream(historyFile));
		try {
			return factory.readChangeDocument(fis, start, end);
		}
		finally {
			fis.close();
		}
	}

}

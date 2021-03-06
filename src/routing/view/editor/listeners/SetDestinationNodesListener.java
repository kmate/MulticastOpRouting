package routing.view.editor.listeners;

import java.awt.event.MouseEvent;
import java.util.Iterator;

import routing.RoutingDemo;
import routing.control.entities.Graph;
import routing.control.entities.Node;
import routing.view.MainFrame;
import routing.view.editor.DocumentEditor;
import routing.view.editor.SessionEditorDialog;

public class SetDestinationNodesListener extends CanvasMouseListener {

	public SetDestinationNodesListener(DocumentEditor editor) {
		super(editor);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Graph net = _editor.getDocument() != null
				&& _editor.getDocument().graph != null ? _editor.getDocument().graph
				: null;

		if (net != null) {
			Node selected = null;
			Iterator<Node> nodeIt = net.getNodeList().iterator();

			while (nodeIt.hasNext()) {
				Node actNode = nodeIt.next();

				if (isNodeAtPoint(actNode, e.getPoint())) {
					selected = actNode;
				}
			}

			if (selected != null && SessionEditorDialog.getInstance().getSession().sourceId != selected.id) {
				SessionEditorDialog.getInstance().toggleDestinationId(selected.id);
				MainFrame mainFrame = RoutingDemo.getMF();
				mainFrame.getCurrentEditor().repaint();
			}
		}
	}
}

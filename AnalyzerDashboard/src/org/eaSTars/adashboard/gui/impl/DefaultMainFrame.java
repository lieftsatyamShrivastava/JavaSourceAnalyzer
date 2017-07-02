package org.eaSTars.adashboard.gui.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.util.Optional;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeSelectionModel;

import org.eaSTars.adashboard.controller.ADashboardGUIConfigController;
import org.eaSTars.adashboard.controller.JavaAssemblyController;
import org.eaSTars.adashboard.controller.JavaSequenceDiagramController;
import org.eaSTars.adashboard.gui.MainFrame;
import org.eaSTars.adashboard.gui.MainFrameAdapter;
import org.eaSTars.adashboard.gui.MainFrameDelegate;
import org.eaSTars.adashboard.gui.dto.ADashboardObjectType;
import org.eaSTars.adashboard.gui.dto.ADashboardObjectView;
import org.eaSTars.adashboard.gui.dto.ViewHistoryEntry;
import org.eaSTars.adashboard.gui.dto.ViewType;

public class DefaultMainFrame extends JFrame implements MainFrame, MainFrameDelegate, TreeWillExpandListener {

	private static final long serialVersionUID = -8684346050770527232L;

	private JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	private MainFrameAdapter mainFrameListener;

	private JavaAssemblyController javaAssemblyController;

	private JavaSequenceDiagramController javaSequenceDiagramController;

	private ADashboardGUIConfigController adashboardGUIController;
	
	private Stack<ViewHistoryEntry> assemblyHistory = new Stack<ViewHistoryEntry>();

	public void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(mainFrameListener);

		buildGUI();

		Optional.ofNullable(adashboardGUIController.getWindowLocation()).ifPresent(l -> setLocation(l));
		
		Dimension dimension = adashboardGUIController.getWindowSize();
		if (dimension != null) {
			setSize(dimension);
		} else {
			dimension = new Dimension(640, 480);
			setSize(dimension);
			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((screensize.width - dimension.width) / 2, (screensize.height - dimension.height) / 2);
		}
		
		mainFrameListener.addFrameClosingListener(w -> {
			adashboardGUIController.setWindowLocation(w.getLocation());
			adashboardGUIController.setWindowSize(w.getSize());
			adashboardGUIController.saveSettings();
		});
	}

	private void buildGUI() {
		JPanel cnt = (JPanel) getContentPane();

		InputMap inputmap = cnt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionmap = cnt.getActionMap();

		String backspace = "backspace";
		inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), backspace);
		actionmap.put(backspace, new AbstractAction() {

			private static final long serialVersionUID = 6767535390505400110L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (assemblyHistory.size() > 1) {
					assemblyHistory.pop();
					ViewHistoryEntry historyentry = assemblyHistory.pop();
					switch(historyentry.getViewType()) {
					case Assembly:
						openAssembly(historyentry.getPK());
						break;
					case Method:
						openMethod(historyentry.getPK());
						break;
					default:
						break;
					}
				}
			}
		});

		splitpane.setLeftComponent(buildTreeView());
		splitpane.setRightComponent(new JScrollPane(new JPanel()));

		cnt.add(splitpane, BorderLayout.CENTER);
	}

	private Component buildTreeView() {
		DefaultMutableTreeNode defaultroot = new DefaultMutableTreeNode();

		javaAssemblyController.getChildAssemblies(null)
		.forEach(a -> defaultroot.add(new ADashboardTreeNode(a)));

		JTree tree = new JTree(defaultroot);
		tree.setRootVisible(false);
		tree.setCellRenderer(new ADashboardTreeCellRenderer());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(tree);

		tree.addTreeWillExpandListener(this);
		tree.addTreeSelectionListener(event -> {
			Object obj = event.getPath().getLastPathComponent();
			if (obj instanceof ADashboardTreeNode && ((ADashboardTreeNode)obj).getUserObject() instanceof ADashboardObjectView) {
				ADashboardTreeNode node = (ADashboardTreeNode) obj;
				ADashboardObjectView jav = (ADashboardObjectView) node.getUserObject();

				if (jav.getType() == ADashboardObjectType.METHOD) {
					openMethod(jav.getId());
				} else if (jav.getType() != ADashboardObjectType.PACKAGE) {
					openAssembly(jav.getId());
				}
			}
		});

		JScrollPane scrollpane = new JScrollPane(tree);
		Dimension minsize = scrollpane.getMinimumSize();
		minsize.width = 150;
		scrollpane.setMinimumSize(minsize);

		return scrollpane;
	}

	@Override
	public void openAssembly(Integer id) {
		openDetailView(id, ViewType.Assembly, javaAssemblyController.getAssemblyFullView(id));
	}

	@Override
	public void openMethod(Integer id) {
		openDetailView(id, ViewType.Method, javaSequenceDiagramController.getSequenceView(id));
	}

	private void openDetailView(Integer id, ViewType viewtype, JPanel panel) {
		if (panel != null) {
			ViewHistoryEntry historyentry = new ViewHistoryEntry();
			historyentry.setViewType(viewtype);
			historyentry.setPK(id);
			assemblyHistory.push(historyentry);
			splitpane.setRightComponent(new JScrollPane(panel));
		} else {
			splitpane.setRightComponent(new JScrollPane(new JPanel()));
		}
	}
	
	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		Object obj = event.getPath().getLastPathComponent();
		ADashboardTreeNode node;
		if (obj instanceof ADashboardTreeNode &&
				(node = (ADashboardTreeNode)obj).getUserObject() instanceof ADashboardObjectView) {
			ADashboardObjectView objectview =  (ADashboardObjectView) node.getUserObject();
			if (objectview.getType() != ADashboardObjectType.METHOD) {
				ADashboardObjectView jav = (ADashboardObjectView) node.getUserObject();
				javaAssemblyController.getChildAssemblies(jav.getId())
				.forEach(a -> node.add(new ADashboardTreeNode(a)));
				javaAssemblyController.getAssemblyMethods(jav.getId())
				.forEach(m -> node.add(new ADashboardTreeNode(m)));
			}

		}
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

	}

	@Override
	public void start() {
		setVisible(true);
	}

	public WindowListener getMainFrameListener() {
		return mainFrameListener;
	}

	public void setMainFrameListener(MainFrameAdapter mainFrameListener) {
		this.mainFrameListener = mainFrameListener;
	}

	public JavaAssemblyController getJavaAssemblyController() {
		return javaAssemblyController;
	}

	public void setJavaAssemblyController(JavaAssemblyController javaAssemblyController) {
		this.javaAssemblyController = javaAssemblyController;
	}

	public JavaSequenceDiagramController getJavaSequenceDiagramController() {
		return javaSequenceDiagramController;
	}

	public void setJavaSequenceDiagramController(JavaSequenceDiagramController javaSequenceDiagramController) {
		this.javaSequenceDiagramController = javaSequenceDiagramController;
	}

	public ADashboardGUIConfigController getAdashboardGUIController() {
		return adashboardGUIController;
	}

	public void setAdashboardGUIController(ADashboardGUIConfigController adashboardGUIController) {
		this.adashboardGUIController = adashboardGUIController;
	}
}

package kairos.kongde;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DND;

@SuppressWarnings("serial")
public class SensorWidget extends Composite {

	public SensorWidget(Composite parent, int style) {
		super(parent, style);
		this.setSize(32, 32);
		
		DragSource dragSource = new DragSource(this, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer()});
	}

}

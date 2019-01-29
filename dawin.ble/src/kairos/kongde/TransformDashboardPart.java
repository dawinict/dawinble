
package kairos.kongde;

import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import javax.annotation.PreDestroy;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;

public class TransformDashboardPart {
	private Table table;

	@PostConstruct
	public void postConstruct(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBounds(0, 0, 637, 568);
		composite.setLayout(new GridLayout(2, false));
		
		Group grpTotalSensor = new Group(composite, SWT.NONE);
		GridData gd_grpTotalSensor = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_grpTotalSensor.widthHint = 300;
		grpTotalSensor.setLayoutData(gd_grpTotalSensor);
		grpTotalSensor.setText("Transform Summary");
		grpTotalSensor.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite_1 = new Composite(grpTotalSensor, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		
		Label lblTotal = new Label(composite_1, SWT.NONE);
		GridData gd_lblTotal = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblTotal.widthHint = 10;
		lblTotal.setLayoutData(gd_lblTotal);
		lblTotal.setText("Total");
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		GridData gd_label = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_label.widthHint = 180;
		label.setLayoutData(gd_label);
		label.setText("20");
		
		Label lblActive = new Label(composite_1, SWT.NONE);
		lblActive.setAlignment(SWT.RIGHT);
		GridData gd_lblActive = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblActive.widthHint = 80;
		lblActive.setLayoutData(gd_lblActive);
		lblActive.setText("Active");
		
		Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		lblNewLabel_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("16");
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		GridData gd_lblNewLabel = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		gd_lblNewLabel.widthHint = 63;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("Stop");
		
		Label lblNewLabel_2 = new Label(composite_1, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		lblNewLabel_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("4");
		
		Group grpLog = new Group(composite, SWT.NONE);
		grpLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		grpLog.setText("Event Log");
		grpLog.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TableViewer tableViewer = new TableViewer(grpLog, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnTime = tableViewerColumn.getColumn();
		tblclmnTime.setWidth(100);
		tblclmnTime.setText("Name");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn_1.getColumn();
		tblclmnNewColumn.setWidth(120);
		tblclmnNewColumn.setText("Topic From");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_1 = tableViewerColumn_2.getColumn();
		tblclmnNewColumn_1.setWidth(120);
		tblclmnNewColumn_1.setText("Topic To");
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnActive = tableViewerColumn_3.getColumn();
		tblclmnActive.setWidth(100);
		tblclmnActive.setText("Active");
		
		Group grpOperatingRate = new Group(composite, SWT.NONE);
		grpOperatingRate.setText("Operating Rate");
		GridData gd_grpOperatingRate = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_grpOperatingRate.widthHint = 300;
		grpOperatingRate.setLayoutData(gd_grpOperatingRate);
		grpOperatingRate.setBounds(0, 0, 70, 92);
		grpOperatingRate.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Browser browser = new Browser(grpOperatingRate, SWT.NONE);
		browser.setText("<!DOCTYPE html>\r\n<html lang=\"en\">\r\n<head>\r\n<meta charset=\"utf-8\">\r\n<title>Animated Donut with Percentage</title>\r\n<style type=\"text/css\">\r\nbody {\r\n    width: 100%;\r\n    height: 100%;\r\n    font-family: Lora,\"Helvetica Neue\",Helvetica,Arial,sans-serif;\r\n    color: #fff;\r\n    //background-color: #000;\r\n}\r\npath.color0 {\r\n    fill: #fff;\r\n}\r\npath.color1 {\r\n    fill: rgba(255,255,255,.3);\r\n}\r\ntext {\r\n    font-size: 7em;\r\n    font-weight: 400;\r\n    line-height: 16em;\r\n    fill: #fff;\r\n}\r\n\r\n</style>\r\n</head>\r\n<body>\r\n\t<svg ></svg>\r\n</body>\r\n<script src=\"https://d3js.org/d3.v3.min.js\"></script>\r\n<script>\r\nd3.select(window).on('resize', resize);\r\n\r\nresize();\r\n\r\nfunction resize() {\r\n\t\r\nd3.selectAll(\"g > *\").remove(); \r\n//var svg = svgOrg.attr(\"width\", window.innerWidth-4).attr(\"height\", window.innerHeight-4)\r\n//.append(\"g\")\r\n //       .attr(\"transform\", \"translate(\" + width / 2 + \",\" + height / 2 + \")\");\r\n\r\nvar dataset = [\r\n        { name: 'Active', count: 16 },\r\n        { name: 'Lost', count: 4 }\r\n    ];\r\n\r\n    var total=0;\r\n\r\n    dataset.forEach(function(d){\r\n        total+= d.count;\r\n    });\r\n\r\n    var ratio=dataset[0].count/total;\r\n\r\n    var pie=d3.layout.pie()\r\n            .value(function(d){return d.count})\r\n            .sort(null);\r\n\r\n    var w=300,h=300;\r\n\r\n    var outerRadius=(w/2)-10;\r\n    var innerRadius=100;\r\n\r\n    var color = d3.scale.ordinal()\r\n     .range(['#67BAF5','#F17F4D']);\r\n\r\n    var arc=d3.svg.arc()\r\n            .innerRadius(innerRadius)\r\n            .outerRadius(outerRadius);\r\n\r\n    var arcLine=d3.svg.arc()\r\n            .innerRadius(innerRadius-13)\r\n            .outerRadius(innerRadius-10)\r\n            .startAngle(0);\r\n\r\n    var svg=d3.select(\"svg\")\r\n            .attr({\r\n                width:w,\r\n                height:h,\r\n                class:'shadow'\r\n            }).append('g')\r\n            .attr({\r\n                transform:'translate('+w/2+','+h/2+')'\r\n            });\r\n    var path=svg.selectAll('path')\r\n            .data(pie(dataset))\r\n            .enter()\r\n            .append('path')\r\n            .attr({\r\n                d:arc,\r\n                fill:function(d,i){\r\n                    return color(d.data.name);\r\n                }\r\n            });\r\n\r\n    var pathLine=svg.append('path')\r\n            .datum({endAngle:0})\r\n            .attr({\r\n                d:arcLine\r\n            })\r\n            .style({\r\n                fill:color('Success')\r\n            });\r\n\r\n    var text=svg.selectAll('.legend')\r\n            .data(pie(dataset))\r\n            .enter()\r\n            .append(\"text\")\r\n            .attr('class','legend')\r\n            .attr(\"transform\", function (d) {\r\n                var c=arc.centroid(d);\r\n                return \"translate(\" +(c[0] *1.11)+','+(c[1]*1.21) + \")\";\r\n            })\r\n            .attr(\"dy\", \".4em\")\r\n            .attr(\"text-anchor\", \"middle\")\r\n            .text(function(d){\r\n                return d.data.name+' ('+Math.round((d.data.count/total)*100)+'%)';\r\n            })\r\n            .style({\r\n                fill:function(d){\r\n                   //return color(d.data.name);\r\n                   return '#000000';\r\n                },\r\n                'font-size':'12px'\r\n            });\r\n\r\n    var middleCount=svg.append('text')\r\n            .datum(0)\r\n            .text(function(d){\r\n                return d;\r\n            })\r\n\r\n            .attr({\r\n                class:'middleText',\r\n                'text-anchor':'middle',\r\n                dy:10\r\n            })\r\n            .style({\r\n                fill:color('Success'),\r\n                'font-size':'35px'\r\n\r\n            });\r\n\r\n    var arcTween=function(transition, newAngle) {\r\n        transition.attrTween(\"d\", function (d) {\r\n            var interpolate = d3.interpolate(d.endAngle, newAngle);\r\n            var interpolateCount = d3.interpolate(0, dataset[0].count);\r\n            return function (t) {\r\n                d.endAngle = interpolate(t);\r\n                middleCount.text(Math.floor(interpolateCount(t)));\r\n                return arcLine(d);\r\n            };\r\n        });\r\n    };\r\n\r\n\r\n    var animate=function(){\r\n        pathLine.transition()\r\n                .duration(750)\r\n                .call(arcTween,((2*Math.PI))*ratio);\r\n\r\n\r\n    };\r\n\r\n    setTimeout(animate,100);\r\n\r\n\r\n}\r\n\r\n\r\n\r\n</script>\r\n");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
	}

	@PreDestroy
	public void preDestroy() {
		
	}

	@Focus
	public void onFocus() {
		
	}

	@Persist
	public void save() {
		
	}
}
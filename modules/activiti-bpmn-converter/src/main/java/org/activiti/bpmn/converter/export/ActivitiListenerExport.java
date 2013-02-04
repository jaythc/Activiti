package org.activiti.bpmn.converter.export;

import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.HasExecutionListeners;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.UserTask;
import org.apache.commons.lang.StringUtils;

public class ActivitiListenerExport implements BpmnXMLConstants {

  public static boolean writeListeners(BaseElement element, boolean didWriteExtensionStartElement, XMLStreamWriter xtw) throws Exception {
    if(element instanceof HasExecutionListeners) {
      didWriteExtensionStartElement =  writeListeners(ELEMENT_EXECUTION_LISTENER, ((HasExecutionListeners) element).getExecutionListeners(), didWriteExtensionStartElement, xtw);
    }
    // In case of a usertaks, also add task-listeners
    if(element instanceof UserTask) {
      didWriteExtensionStartElement =  writeListeners(ELEMENT_TASK_LISTENER, ((UserTask) element).getTaskListeners(), didWriteExtensionStartElement, xtw);
    }
    return didWriteExtensionStartElement;
  }
  
  private static boolean writeListeners(String xmlElementName, List<ActivitiListener> listenerList, boolean didWriteExtensionStartElement, XMLStreamWriter xtw) throws Exception {
    if (listenerList != null) {
      
      for (ActivitiListener listener : listenerList) {
        
        if (StringUtils.isNotEmpty(listener.getEvent())) {
          
          if (!didWriteExtensionStartElement) { 
            xtw.writeStartElement(ELEMENT_EXTENSIONS);
            didWriteExtensionStartElement = true;
          }
          
          xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, xmlElementName, ACTIVITI_EXTENSIONS_NAMESPACE);
          BpmnXMLUtil.writeDefaultAttribute(ATTRIBUTE_LISTENER_EVENT, listener.getEvent(), xtw);
          
          if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(listener.getImplementationType())) {
            BpmnXMLUtil.writeDefaultAttribute(ATTRIBUTE_LISTENER_CLASS, listener.getImplementation(), xtw);
          } else if (ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equals(listener.getImplementationType())) {
            BpmnXMLUtil.writeDefaultAttribute(ATTRIBUTE_LISTENER_EXPRESSION, listener.getImplementation(), xtw);
          } else if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equals(listener.getImplementationType())) {
            BpmnXMLUtil.writeDefaultAttribute(ATTRIBUTE_LISTENER_DELEGATEEXPRESSION, listener.getImplementation(), xtw);
          }
          
          FieldExtensionExport.writeFieldExtensions(listener.getFieldExtensions(), true, xtw);
          
          xtw.writeEndElement();
        }
      }
    }
    return didWriteExtensionStartElement;
  }
  
}

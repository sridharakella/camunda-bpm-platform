/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.engine.test.api.multitenancy.tenantcheck;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DiagramLayout;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.util.ProcessEngineTestRule;
import org.camunda.bpm.engine.test.util.ProvidedProcessEngineRule;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

/**
 * @author kristin.polenz
 */
public class MultiTenancyProcessDefinitionCmdsTenantCheckTest {

  protected static final String TENANT_ONE = "tenant1";

  protected static final String BPMN_PROCESS_MODEL = "org/camunda/bpm/engine/test/api/multitenancy/testProcess.bpmn";
  protected static final String BPMN_PROCESS_DIAGRAM = "org/camunda/bpm/engine/test/api/multitenancy/testProcess.png";

  protected ProcessEngineRule engineRule = new ProvidedProcessEngineRule();

  protected ProcessEngineTestRule testRule = new ProcessEngineTestRule(engineRule);

  @Rule
  public RuleChain ruleChain = RuleChain.outerRule(engineRule).around(testRule);

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  protected RepositoryService repositoryService;
  protected IdentityService identityService;
  protected ProcessEngineConfigurationImpl processEngineConfiguration;

  protected String processDefinitionId;

  @Before
  public void setUp() {
    processEngineConfiguration = engineRule.getProcessEngineConfiguration();
    repositoryService = engineRule.getRepositoryService();
    identityService = engineRule.getIdentityService();

    testRule.deployForTenant(TENANT_ONE, BPMN_PROCESS_MODEL, BPMN_PROCESS_DIAGRAM);

    processDefinitionId = repositoryService.createProcessDefinitionQuery().singleResult().getId();
  }

  @Test
  public void failToGetProcessModelNoAuthenticatedTenants() {
    identityService.setAuthentication("user", null, null);

    // declare expected exception
    thrown.expect(ProcessEngineException.class);
    thrown.expectMessage("Cannot get the process definition");

    repositoryService.getProcessModel(processDefinitionId);
  }

  @Test
  public void getProcessModelWithAuthenticatedTenant() {
    identityService.setAuthentication("user", null, Arrays.asList(TENANT_ONE));

    InputStream inputStream = repositoryService.getProcessModel(processDefinitionId);

    assertThat(inputStream, notNullValue());
  }

  @Test
  public void getProcessModelDisabledTenantCheck() {
    processEngineConfiguration.setTenantCheckEnabled(false);
    identityService.setAuthentication("user", null, null);

    InputStream inputStream = repositoryService.getProcessModel(processDefinitionId);

    assertThat(inputStream, notNullValue());
  }

  @Test
  public void failToGetProcessDiagramNoAuthenticatedTenants() {
    identityService.setAuthentication("user", null, null);

    // declare expected exception
    thrown.expect(ProcessEngineException.class);
    thrown.expectMessage("Cannot get the process definition");

    repositoryService.getProcessDiagram(processDefinitionId);
  }

  @Test
  public void getProcessDiagramWithAuthenticatedTenant() {
    identityService.setAuthentication("user", null, Arrays.asList(TENANT_ONE));

    InputStream inputStream = repositoryService.getProcessDiagram(processDefinitionId);

    assertThat(inputStream, notNullValue());
  }

  @Test
  public void getProcessDiagramDisabledTenantCheck() {
    processEngineConfiguration.setTenantCheckEnabled(false);
    identityService.setAuthentication("user", null, null);

    InputStream inputStream = repositoryService.getProcessDiagram(processDefinitionId);

    assertThat(inputStream, notNullValue());
  }

  @Test
  public void failToGetProcessDiagramLayoutNoAuthenticatedTenants() {
    identityService.setAuthentication("user", null, null);

    // declare expected exception
    thrown.expect(ProcessEngineException.class);
    thrown.expectMessage("Cannot get the process definition");

    repositoryService.getProcessDiagramLayout(processDefinitionId);
  }

  @Test
  public void getProcessDiagramLayoutWithAuthenticatedTenant() {
    identityService.setAuthentication("user", null, Arrays.asList(TENANT_ONE));

    DiagramLayout diagramLayout = repositoryService.getProcessDiagramLayout(processDefinitionId);

    assertThat(diagramLayout, notNullValue());
  }

  @Test
  public void getProcessDiagramLayoutDisabledTenantCheck() {
    processEngineConfiguration.setTenantCheckEnabled(false);
    identityService.setAuthentication("user", null, null);

    DiagramLayout diagramLayout = repositoryService.getProcessDiagramLayout(processDefinitionId);

    assertThat(diagramLayout, notNullValue());
  }

  @Test
  public void failToGetProcessDefinitionNoAuthenticatedTenants() {
    identityService.setAuthentication("user", null, null);

    // declare expected exception
    thrown.expect(ProcessEngineException.class);
    thrown.expectMessage("Cannot get the process definition");

    repositoryService.getProcessDefinition(processDefinitionId);
  }

  @Test
  public void getProcessDefinitionWithAuthenticatedTenant() {
    identityService.setAuthentication("user", null, Arrays.asList(TENANT_ONE));

    ProcessDefinition definition = repositoryService.getProcessDefinition(processDefinitionId);

    assertThat(definition.getTenantId(), is(TENANT_ONE));
  }

  @Test
  public void getProcessDefinitionDisabledTenantCheck() {
    processEngineConfiguration.setTenantCheckEnabled(false);
    identityService.setAuthentication("user", null, null);

    ProcessDefinition definition = repositoryService.getProcessDefinition(processDefinitionId);

    assertThat(definition.getTenantId(), is(TENANT_ONE));
  }

  @Test
  public void failToGetBpmnModelInstanceNoAuthenticatedTenants() {
    identityService.setAuthentication("user", null, null);

    // declare expected exception
    thrown.expect(ProcessEngineException.class);
    thrown.expectMessage("Cannot get the process definition");

    repositoryService.getBpmnModelInstance(processDefinitionId);
  }

  @Test
  public void getBpmnModelInstanceWithAuthenticatedTenant() {
    identityService.setAuthentication("user", null, Arrays.asList(TENANT_ONE));

    BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);

    assertThat(modelInstance, notNullValue());
  }

  @Test
  public void getBpmnModelInstanceDisabledTenantCheck() {
    processEngineConfiguration.setTenantCheckEnabled(false);
    identityService.setAuthentication("user", null, null);

    BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);

    assertThat(modelInstance, notNullValue());
  }

  @Test
  public void failToDeleteProcessDefinitionNoAuthenticatedTenant() {
    //given deployment with a process definition
    List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
    //and user with no tenant authentication
    identityService.setAuthentication("user", null, null);

    // declare expected exception
    thrown.expect(ProcessEngineException.class);
    thrown.expectMessage("Cannot delete the process definition");

    //deletion should end in exception, since tenant authorization is missing
    repositoryService.deleteProcessDefinition(processDefinitions.get(0).getId());
  }

  @Test
  public void testDeleteProcessDefinitionWithAuthenticatedTenant() {
    //given deployment with two process definitions
    Deployment deployment = testRule.deployForTenant(TENANT_ONE, "org/camunda/bpm/engine/test/repository/twoProcesses.bpmn20.xml");
    ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId());
    List<ProcessDefinition> processDefinitions = processDefinitionQuery.list();
    //and user with tenant authentication
    identityService.setAuthentication("user", null, Arrays.asList(TENANT_ONE));

    //when delete process definition with authenticated user
    repositoryService.deleteProcessDefinition(processDefinitions.get(0).getId());

    //then process definition should be deleted
    identityService.clearAuthentication();
    assertThat(processDefinitionQuery.count(), is(1L));
    assertThat(processDefinitionQuery.tenantIdIn(TENANT_ONE).count(), is(1L));
  }

  @Test
  public void testDeleteCascadeProcessDefinitionWithAuthenticatedTenant() {
    //given deployment with a process definition and process instance
    BpmnModelInstance bpmnModel = Bpmn.createExecutableProcess("process").startEvent().userTask().endEvent().done();
    testRule.deployForTenant(TENANT_ONE, bpmnModel);
    ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
    ProcessDefinition processDefinition = processDefinitionQuery.processDefinitionKey("process").singleResult();
    engineRule.getRuntimeService().createProcessInstanceByKey("process").executeWithVariablesInReturn();

    //and user with tenant authentication
    identityService.setAuthentication("user", null, Arrays.asList(TENANT_ONE));

    //when the corresponding process definition is cascading deleted from the deployment
    repositoryService.deleteProcessDefinition(processDefinition.getId(), true);

    //then exist no process instance and one definition
    identityService.clearAuthentication();
    assertEquals(0, engineRule.getRuntimeService().createProcessInstanceQuery().count());
    if (processEngineConfiguration.getHistoryLevel().getId() >= HistoryLevel.HISTORY_LEVEL_ACTIVITY.getId()) {
      assertEquals(0, engineRule.getHistoryService().createHistoricActivityInstanceQuery().count());
    }
    assertThat(repositoryService.createProcessDefinitionQuery().count(), is(1L));
    assertThat(repositoryService.createProcessDefinitionQuery().tenantIdIn(TENANT_ONE).count(), is(1L));
  }

  @Test
  public void testDeleteProcessDefinitionDisabledTenantCheck() {
    //given deployment with two process definitions
    Deployment deployment = testRule.deployForTenant(TENANT_ONE, "org/camunda/bpm/engine/test/repository/twoProcesses.bpmn20.xml");
    //tenant check disabled
    processEngineConfiguration.setTenantCheckEnabled(false);
    ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId());
    List<ProcessDefinition> processDefinitions = processDefinitionQuery.list();
    //user with no authentication
    identityService.setAuthentication("user", null, null);

    //when process definition should be deleted without tenant check
    repositoryService.deleteProcessDefinition(processDefinitions.get(0).getId());

    //then process definition is deleted
    identityService.clearAuthentication();
    assertThat(processDefinitionQuery.count(), is(1L));
    assertThat(processDefinitionQuery.tenantIdIn(TENANT_ONE).count(), is(1L));
  }

  @Test
  public void testDeleteCascadeProcessDefinitionDisabledTenantCheck() {
    //given deployment with a process definition and process instances
    BpmnModelInstance bpmnModel = Bpmn.createExecutableProcess("process").startEvent().userTask().endEvent().done();
    testRule.deployForTenant(TENANT_ONE, bpmnModel);
    //tenant check disabled
    processEngineConfiguration.setTenantCheckEnabled(false);
    ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
    ProcessDefinition processDefinition = processDefinitionQuery.processDefinitionKey("process").singleResult();
    engineRule.getRuntimeService().createProcessInstanceByKey("process").executeWithVariablesInReturn();
    //user with no authentication
    identityService.setAuthentication("user", null, null);

    //when the corresponding process definition is cascading deleted from the deployment
    repositoryService.deleteProcessDefinition(processDefinition.getId(), true);

    //then exist no process instance and one definition, because test case deployes per default one definition
    identityService.clearAuthentication();
    assertEquals(0, engineRule.getRuntimeService().createProcessInstanceQuery().count());
    if (processEngineConfiguration.getHistoryLevel().getId() >= HistoryLevel.HISTORY_LEVEL_ACTIVITY.getId()) {
      assertEquals(0, engineRule.getHistoryService().createHistoricActivityInstanceQuery().count());
    }
    assertThat(repositoryService.createProcessDefinitionQuery().count(), is(1L));
    assertThat(repositoryService.createProcessDefinitionQuery().tenantIdIn(TENANT_ONE).count(), is(1L));
  }

}

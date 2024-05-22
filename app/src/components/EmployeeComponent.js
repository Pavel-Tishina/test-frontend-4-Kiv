// main page
import React from "react";
import EmployeeService from "../services/EmployeeService";
import Pagination from 'react-bootstrap/Pagination';
import Card from 'react-bootstrap/Card';
import Button from 'react-bootstrap/Button';
import Alert from 'react-bootstrap/Alert';
import Modal from 'react-bootstrap/Modal';
import Form from 'react-bootstrap/Form';

class EmployeeComponent extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            activePage          : props.activePage || undefined,
            totalN              : undefined,
            limitN              : 5,
            employees           : [],
            dialogType          : undefined,
            showDialog          : false,
            selectedEmp         : undefined,
            reqPutKeys          : ['id','firstName','lastName','position','supervisor'],
            reqPostKeys         : ['firstName','lastName','position','supervisor'],
            showAlert           : false,
            msgAlert            : '',
            typeAlert           : '',
            possibleSupervisors : []
        }

        this.setActivePage = this.setActivePage.bind(this);
        this.maxPage = this.maxPage.bind(this);

        this.undefSelectedEmployeeData = this.undefSelectedEmployeeData.bind(this);
        this.setFieldValue = this.setFieldValue.bind(this);

        this.closeAlert = this.closeAlert.bind(this);
        this.showAlert = this.showAlert.bind(this);

        this.closeDialog = this.closeDialog.bind(this);
        this.showDialog = this.showDialog.bind(this);

        this.getCardID = this.getCardID.bind(this);

        this.getFromSelectEmp = this.getFromSelectEmp.bind(this);

        this.saveChanges = this.saveChanges.bind(this);
        this.removeEmployee = this.removeEmployee.bind(this);
        this.getChangesJSON = this.getChangesJSON.bind(this);
        this.getNewEmployeeJSON = this.getNewEmployeeJSON.bind(this);

        this.getChildrenTitle = this.getChildrenTitle.bind(this);
    }

    componentDidMount() {
        const { activePage, totalN, limitN } = this.state;
        
        if (totalN === undefined || activePage === undefined) {
            EmployeeService.getTotal().then((response) => {
                console.dir(response.data.total);
                this.setState({ totalN: response.data.total });
                this.setState({ activePage: Math.floor(response.data.total / this.state.limitN / 2) });
                console.log(this.state.totalN + "\t" + this.state.activePage);
            }, (error) => {
                console.log(error);
                this.setState({ totalN: 0 });
                this.setState({ activePage: 0 });
            });
        }

        const p = activePage !== undefined && activePage >= 0
            ? activePage
            : 0;

        EmployeeService.getEmployeePage(p, limitN).then((response) => {
            console.log(response.data);
            this.setState({ employees: response.data.result})
          }, (error) => {
            console.log(error);
            this.setState({ employees: []})
          });

    }

    maxPage() {
        return Math.floor(this.state.totalN / this.state.limitN);
    }

    setActivePage = (n) => {
        if (n < 0) {
            this.setState({ activePage : 0})
        }
        else if (n > this.maxPage()) {
            this.setState({ activePage : this.maxPage() })
        }
        else {
            this.setState({ activePage : n})
        }

        this.componentDidMount();
    }


    undefSelectedEmployeeData() {
        this.setState({
            selectedEmp         : undefined,
            possibleSupervisors : []
        });
    }

    
    setFieldValue = (event, fieldName) => {
        const { selectedEmp }  = this.state;
        console.dir('selectedEmp = ' + selectedEmp);

        if (selectedEmp !== null && selectedEmp !== undefined) {
            if (fieldName === 'firstName') {
                selectedEmp["firstName"] = event.target.value;
              } 
              else if (fieldName === 'lastName') {
                selectedEmp["lastName"] = event.target.value;
              } 
              else if (fieldName === 'position') {
                selectedEmp["position"] = event.target.value;
              }
              else if (fieldName === 'supervisor') {
                selectedEmp["supervisor"] = event.target.value;
                selectedEmp["supervisorFullName"] = this.getChildrenTitle(event.target.children, event.target.value);
              }

            this.setState({ selectedEmp : selectedEmp });  
        }
    };

    getChildrenTitle(childrenCollection, id) {
        for (let i = 0; i < childrenCollection.length; i++) {
            const option = childrenCollection[i];

            if (option.innerText.endsWith('(' + id + ')')) {
                return option.innerText;
            }
        }

        return '';
    }

    getCardID(id) {
        return 'card-' + id;
    }

    saveChanges = () => {
        const { selectedEmp, dialogType } = this.state;
        console.dir(selectedEmp);

        if (dialogType === 'employee') {
            EmployeeService.putEmployee(this.getChangesJSON(selectedEmp)).then((response) => {
                this.showAlert('success', 'Changes was saved!');
    
                window.setTimeout(()=>{
                    this.closeDialog();
                    window.location.reload();
                    },1500)
                })
                .catch((error) => {
                    console.dir('HHHHHSSS = ' +  error.response.data.errors);
                    this.showAlert('danger', error.response.data.errors);
                  });
        }
        else if (dialogType === 'add') {
            EmployeeService.postEmployee(this.getNewEmployeeJSON(selectedEmp)).then((response) => {
                this.showAlert('success', 'Changes was saved!');
    
                window.setTimeout(()=>{
                    this.closeDialog();
                    window.location.reload();
                    },1500)
                })
                .catch((error) => {
                    console.dir('HHHHHSSS = ' +  error.response.data.errors);
                    this.showAlert('danger', error.response.data.errors);
                  });
        }
    };

    removeEmployee = () => {
        const { selectedEmp } = this.state;
        console.dir('REMOOOOOVE = ' + selectedEmp);

        EmployeeService.delEmployeeById(selectedEmp["id"]).then((response) => {
            this.showAlert('success', 'Employee was removed!');

            window.setTimeout(()=>{
                this.closeDialog();
                window.location.reload();
                },1500)
            })
            .catch((error) => {
                console.dir('HHHHHSSS = ' +  error.response.data.errors);
                this.showAlert('danger', error.response.data.errors);
              });
    }

    getChangesJSON(selectedEmp) {
        const { reqPutKeys } = this.state;
        return Object.keys(selectedEmp)
                .filter(key => reqPutKeys.includes(key))
                .reduce((obj, key) => {
                    obj[key] = selectedEmp[key];
                    return obj;
                }, {});
    }

    getNewEmployeeJSON(selectedEmp) {
        const { reqPostKeys } = this.state;
        return Object.keys(selectedEmp)
                .filter(key => reqPostKeys.includes(key))
                .reduce((obj, key) => {
                    obj[key] = selectedEmp[key];
                    return obj;
                }, {});
    }

    closeDialog = () => {
        this.setState({ showDialog: false });
    };  

    showDialog = (type, id) => {
        this.undefSelectedEmployeeData();


        EmployeeService.getPossibleSupervisors(id).then((response) => {
            this.setState({ possibleSupervisors : response.data.result });            
            })
            .catch((error) => {
                console.dir('HHHHHSSS = ' +  error.response.data.errors);
                this.showAlert('danger', error.response.data.errors);
              });

    
        console.log('ids instance ' + (typeof id) + "\t" + id);
    
        if (typeof id === 'string') {
            console.log('id === ' + id);
            const selectedEmp2Store = { ...this.state.employees.find(emp => emp.id === id) };
        
            console.log('selectedEmp11 === ' + selectedEmp2Store["id"]);

            this.setState({ selectedEmp: selectedEmp2Store });
        } else if (id === null) {
            console.log('IS NULL ' + id);

            const selectedEmp2Store = { ...this.state.employees[0]};
            selectedEmp2Store["firstName"] = "";
            selectedEmp2Store["lastName"] = "";
            selectedEmp2Store["position"] = "";
            selectedEmp2Store["supervisor"] = "";
            
            this.setState({ selectedEmp : selectedEmp2Store });
        }
        const { selectedEmp } = this.state;

        console.log('SEEEELEEECTEDDD = ' + selectedEmp);
        
        this.setState({ showDialog: true, dialogType: type });
    };

    closeAlert = () => {
        this.setState({ showAlert: false });
    };  

    showAlert = (type, msg) => {
        
        this.setState({ showAlert: true, msgAlert : msg, typeAlert: type }, ()=>{
            window.setTimeout(()=>{
                this.setState({showAlert : false})
              },2500)
            });
    };

    getFromSelectEmp = (field) => {
        const { selectedEmp } = this.state;
        return selectedEmp !== null && selectedEmp !== undefined
            ? selectedEmp[field]
            : ''
    }


    render (){
        const { activePage, employees, showDialog, dialogType, showAlert, msgAlert, typeAlert, possibleSupervisors } = this.state;

        return (
            <>
                {employees.map(emp => (
                    <Card style={{ 
                        width: '40rem', 
                        textAlign: 'left', 
                        cursor: 'pointer' }}
                        id={this.getCardID(emp["id"])}
                    >
                        <Card.Body>    
                        <Card.Title >{emp["fullName"]}</Card.Title>
                        <Card.Text >
                            <div>Position: {emp["position"]}
                            {emp["supervisorFullName"] !== '' &&(
                                <p>Supervisor: {emp["supervisorFullName"]}</p>
                            )}
                            </div>
                        </Card.Text>
                            <div style={{ display: 'flex', justifyContent: 'space-between' }} >
                                <Button variant="primary" 
                                    onClick={() => this.showDialog('employee', emp["id"])}>
                                        Employee info</Button>
                            </div>            
                        </Card.Body>
                    </Card>
                ))}

                <div style={{ display: 'flex', justifyContent: 'space-between', width: '40rem' }}>
                    <Pagination>
                        <Pagination.First onClick={() => this.setActivePage(0)} />
                        <Pagination.Prev onClick={() => this.setActivePage(activePage - 1)}/>
                        
                        <Pagination.Item active>{activePage}</Pagination.Item>
                    
                        <Pagination.Next onClick={() => this.setActivePage(activePage + 1)} />
                        <Pagination.Last onClick={() => this.setActivePage(this.maxPage())} />
                    </Pagination>

                    <Button variant="primary" onClick={() => this.showDialog('add', null)}>Add</Button>
                </div>




                <Modal show={showDialog} onHide={this.closeDialog} className="overlay">
                    <Alert show={showAlert} variant={typeAlert}>
                        {msgAlert}
                    </Alert>
                    <Modal.Header closeButton>
                        <Modal.Title>
                            {dialogType === 'employee' && (
                                <p>Information about Employye</p>
                            )}
                            {dialogType === 'add' && (
                                <p>Add new Employee</p>
                            )}
                            {dialogType === 'del' && (
                                <p>Delete selected Employees</p>
                            )}
                        </Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        {dialogType === 'del' && (
                            <p>Do you want delete users?</p>
                        )}


                        {dialogType !== 'add' && dialogType !== 'del' && (
                            <div>
                                <Form.Label>ID</Form.Label>
                                <Form.Control value={this.getFromSelectEmp("id")} readOnly />
                                <Form.Text id="field-id" />
                            </div>  
                        )}
                        {dialogType !== 'del' && (
                            <div>
                                <div>
                                    <Form.Label>First Name</Form.Label>
                                    <Form.Control value={this.getFromSelectEmp("firstName")}
                                        onChange={(e) => this.setFieldValue(e, 'firstName')}
                                    />
                                    <Form.Text id="field-first-name" />
                                </div>
                                <div>
                                    <Form.Label>Last Name</Form.Label>
                                    <Form.Control value={this.getFromSelectEmp("lastName")}
                                        onChange={(e) => this.setFieldValue(e, 'lastName')}
                                    />
                                    <Form.Text id="field-last-name" />
                                </div>
                                <div>
                                    <Form.Label>Position</Form.Label>
                                    <Form.Control value={this.getFromSelectEmp("position")}
                                        onChange={(e) => this.setFieldValue(e, 'position')}
                                    />
                                    <Form.Text id="field-position" />
                                </div>
                                <div>
                                    <Form.Label>Supervisor</Form.Label>
                                    <Form.Select aria-label="Default select example" onChange={(e) => this.setFieldValue(e, 'supervisor')} 
                                        value={this.getFromSelectEmp("supervisor")} 
                                    >
                                        <option value=""></option>
                                        {(this.getFromSelectEmp("supervisor") !== '' || this.getFromSelectEmp("supervisor") > 0) && (
                                            <option value={this.getFromSelectEmp("supervisor")} active>{this.getFromSelectEmp("supervisorFullName")}</option>
                                        )}
                                        {possibleSupervisors.map(sv => (
                                            sv["id"] !== this.getFromSelectEmp("supervisor") && (
                                                <option value={sv["id"]}>{sv["fullName"]}</option>
                                            )
                                        ))}
                                    </Form.Select>
                                </div> 
                            </div>
                        )}    
                        {dialogType !== 'add' && dialogType !== 'del' && (
                            <div>    
                                <Form.Label>Created</Form.Label>
                                <Form.Control value={this.getFromSelectEmp("created")} readOnly/>
                                <Form.Text id="field-created" />
                            </div>  
                        )}
                    </Modal.Body>
                    <Modal.Footer>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }} >
                            <Button variant="secondary" onClick={this.closeDialog}>
                                Close
                            </Button>
                            {(dialogType === 'add' || dialogType === 'employee') && (
                                
                                    <Button variant="primary" onClick={this.saveChanges}>
                                        Save Changes
                                    </Button>
                            )}
                            {(dialogType === 'employee') && (        
                                    <Button variant="danger" onClick={this.removeEmployee}>
                                        Remove
                                    </Button>
                                
                            )}
                        </div>
                    </Modal.Footer>
                </Modal>
            </>
        )
    }
}

export default EmployeeComponent
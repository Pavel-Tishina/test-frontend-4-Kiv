// element of list
import React, {useState} from "react";
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import Modal from 'react-bootstrap/Modal';


class EmployeeElementComponent extends React.Component {
    
    constructor(props) {
        super(props)
        this.state = {
            id           : this.props.id || -1,
            firstName    : this.props.firstName || 'John',
            lastName     : this.props.lastName || 'Doe',
            position     : this.props.position || 'Unknown',
            supId        : this.props.supId || -1,
            supFirstName : this.props.supFirstName || 'Jane',
            supLastName  : this.props.supLastName || 'Doe',
            cardColor    : 'white',
            selected     : false,
            showDialog   : false,
            dialogType   : 'employee'
        }

        this.handleClick = this.handleClick.bind(this);
        this.openDialogEmployee = this.openDialogEmployee.bind(this);
        this.openDialogSupervisor = this.openDialogSupervisor.bind(this);
        this.closeDialog = this.closeDialog.bind(this);

        this.test = this.test.bind(this);
    }

    handleClick = (event) => {
        if (event.target.tagName !== "BUTTON") {
            console.log(!this.state.selected);
            const newColor = this.state.cardColor === 'white' ? 'lightblue' : 'white';
            const newSel = !this.state.selected;
            this.setState({ cardColor: newColor });
            this.setState({ selected: newSel });
        }
    };

    openDialogEmployee = () => { 
        this.setState({ showDialog: true, dialogType: 'employee' });

        
    };
      
    openDialogSupervisor = () => {
        this.setState({ showDialog: true, dialogType: 'supervisor' });
        
    };

    closeDialog = () => {
        this.setState({ showDialog: false });
        
    };


    test = () => {
        console.log('Type ' + this.state.dialogType);
        
    };


    render (){
        const { id, firstName, lastName, position, supId, supFirstName, supLastName, cardColor, selected, dialogType, showDialog } = this.state;

        return (
            <div>
            <Card style={{ 
                width: '40rem', 
                textAlign: 'left', 
                backgroundColor: cardColor, 
                cursor: 'pointer',
                border: selected ? "2px solid blue" : "1px solid #ccc"
                }}
                
                onClick={this.handleClick}            
            >
                <Card.Body>    
                    <Card.Title >{firstName} {lastName} ({id})</Card.Title>
                    <Card.Text >
                        <div>Position: {position}<p>Supervisor: {supFirstName} {supLastName} ({supId})</p></div>
                    </Card.Text>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }} >
                        <Button variant="primary" onClick={this.openDialogEmployee}>Employee info</Button>
                        {supId !== undefined || supId >= 0 && (
                            <Button variant="secondary" onClick={this.openDialogSupervisor}>Supervisor info</Button>
                        )}
                        
                    </div>            
                </Card.Body>
            </Card>
            
            <Modal show={showDialog} onHide={this.closeDialog} className="overlay">
                <Modal.Header closeButton>
                    <Modal.Title>Modal heading</Modal.Title>
                </Modal.Header>
                <Modal.Body>Woohoo, you are reading this text in a modal!</Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={this.closeDialog}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={this.closeDialog}>
                        Save Changes
                    </Button>
                </Modal.Footer>
            </Modal>

        </div>
        )
    }
}

export default EmployeeElementComponent;
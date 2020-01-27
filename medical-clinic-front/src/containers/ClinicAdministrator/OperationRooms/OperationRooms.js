import React, { Component } from 'react';
import axios from '../../../axios';
import ReactTable from 'react-table-6';
import Auxiliary from '../../../hoc/Auxiliary/Auxiliary';
import moment from 'moment';

import Button from '../../../components/UI/Button/Button';
import RoomAppointments from './RoomAppointments/RoomAppointments';
import Modal from '../../../components/UI/Modal/Modal';
import plusimg from '../../../assets/images/plus.png';
import classes from './OperationRooms.module.css';
import OperationRoomForm from '../../../components/Forms/OperationRoomForm/OperationRoomForm';

class OperationRooms extends Component {

    state = {
        operationRooms: null,
        roomId: null,
        showRoomRedails: false,
        appointments: null,
        roomName: null,
        roomNumber: null,
        pickedDate: null,
        availableSchedule: null,
        modalOpen: false,
        room: {
            name: '',
            number: ''
        },
        addForm: false
    }

    componentDidMount() {
        this.getOperationRooms();
    }

    getOperationRooms = () => {
        axios.get('/operationRoom/getAll')
            .then(rooms => {
                this.setState({ operationRooms: rooms.data })
            })
            .catch(err => console.log(err));
    }

    checkAvailability = () => {
        const pickedDate = moment(this.state.pickedDate).valueOf();

        this.state.appointments.map(app => {
            if (app.date > pickedDate && app.date < (pickedDate + (60 * 60000))) {
                this.setState({ availableSchedule: false });
            } else {
                this.setState({ availableSchedule: true });
            }
        })

    }

    closeModalHandler = () => {
        this.setState({
            modalOpen: false,
            room: {
                name: '',
                number: ''
            }
        });
    }

    openModalHandler = () => {
        this.setState({ modalOpen: true });
    }

    showScheduleHandler = (operationRoom) => {

        this.setState({ roomId: operationRoom.roomId });
        axios.get("/appointment/getAllByOperationRoom/" + operationRoom.roomId)
            .then(app => {
                this.setState({ appointments: app.data, roomName: operationRoom.name, roomNumber: operationRoom.number,showRoomRedails: true, addForm: false });
            })
            .catch(err => {
                this.setState({showRoomRedails: false, addForm: false});
                alert('Unable to schedule. \nREASON: '+ err.response.data);
            });

        this.setState({ availableSchedule: null });
    }

    editRoomHandler = (operationRoom) => {
        console.log(operationRoom);
        this.setState({ room: operationRoom, modalOpen: true });
    }

    removeRoomHandler = (operationRoom) => {
        axios.post('/operationRoom/delete/' + operationRoom.roomId, null)
            .then(response => {
                let rooms = [...this.state.operationRooms];

                const index = this.state.operationRooms.indexOf(operationRoom);
                if (index > -1) {
                    rooms.splice(index, 1);
                }

                this.setState({ operationRooms: rooms });
            })
            .catch(err => alert('Unable to remove room.\nReason: ' + err.response.data));
    }

    pushNewRoom = (newRoom) => {
        let rooms = [...this.state.operationRooms];
        rooms.push(newRoom);
        this.setState({ operationRooms: rooms });
    }

    replaceRoom = (newRoom) => {
        let rooms = [...this.state.operationRooms];

        rooms.map(el => {
            if (el.roomId === newRoom.roomId) {
                el.name = newRoom.name;
                el.number = newRoom.number;
            }
            return el;
        })

        this.setState({ operationRooms: rooms });

    }

    showHideHandler = (boolean) => {
        this.setState({ showRoomRedails: boolean, addForm: false });
    }



    render() {
        let table = null;
        let roomDetails = null;

        if (this.state.operationRooms !== null) {
            const data = this.state.operationRooms;

            const columns = [{
                Header: (
                    <div>
                        <div><span>List of all rooms</span></div>
                    </div>
                ),
                columns: [
                    {
                        id: 'name',
                        Header: 'Name',
                        accessor: d => d.name
                    },
                    {
                        id: 'number',
                        Header: 'Number',
                        accessor: d => d.number
                    },
                    {
                        Header: "",
                        Cell: ({ original }) => (
                            <center><Button type='green' click={() => this.showScheduleHandler(original)}>Schedule</Button></center>),
                        filterable: false,
                        sortable: false
                    },
                    {
                        Header: "",
                        Cell: ({ original }) => (
                            <center><Button type='black' click={() => this.editRoomHandler(original)}>Edit</Button></center>),
                        filterable: false,
                        sortable: false
                    },
                    {
                        Header: "",
                        Cell: ({ original }) => (
                            <center><Button type='red' click={() => this.removeRoomHandler(original)}>Remove</Button></center>),
                        filterable: false,
                        sortable: false
                    }]
            }];

            table = (
                <ReactTable
                    data={data}
                    columns={columns}
                    className="-striped -highlight"
                    pageSize={10}
                    filterable={true}
                    pageSize={(this.state.operationRooms.length > 5) ? 5 : this.state.operationRooms.length}
                    defaultFilterMethod={(filter, row, column) => {
                        const id = filter.pivotId || filter.id
                        return row[id] !== undefined ? String(row[id]).toLowerCase().includes(filter.value.toLowerCase()) : true
                    }}
                />

            );
        }

        if (this.state.showRoomRedails !== false && this.state.appointments !== null) {

            roomDetails = <RoomAppointments
                roomName={this.state.roomName}
                roomNumber={this.state.roomNumber}
                appointments={this.state.appointments}
                back={this.showHideHandler} />
        } else if (this.state.addForm) {
            roomDetails = <div className="login-form-1"><OperationRoomForm header={"Create new room"} closeModal={this.closeModalHandler} pushNewRoom={this.pushNewRoom} back={this.showHideHandler} /></div>
        }

        return (
            <Auxiliary>
                <div
                    className="col-7 login-form-1"
                    style={{ marginBottom: '2.5%', marginTop: 'auto', marginLeft: 'auto', marginRight: 'auto', padding: '2.5%' }}
                    hidden={this.state.showRoomRedails || this.state.addForm}>

                    <div style={{ display: 'flex' }}>
                        <h4>Add new room</h4>
                        <div style={{ margin: '0px 10px' }} onClick={() => this.setState({ addForm: true, showRoomRedails: false })}><img src={plusimg} className={classes.Image} /></div>
                    </div>
                    {table}
                </div>
                <div
                    className='col-7 login-form-1'
                    style={{ marginBottom: '2.5%', marginTop: 'auto', marginLeft: 'auto', marginRight: 'auto', padding: '2.5%'}}
                    hidden={!this.state.showRoomRedails}>
                    
                    {roomDetails}
                </div>

                <div
                    className='col-7'
                    style={{ marginBottom: '2.5%', marginTop: 'auto', marginLeft: 'auto', marginRight: 'auto', padding: '2.5%'}}
                    hidden={!this.state.addForm}>
                    
                    {roomDetails}
                </div>
                <Modal show={this.state.modalOpen} modalClosed={this.closeModalHandler}>
                    <OperationRoomForm back={this.showHideHandler} room={this.state.room} closeModal={this.closeModalHandler} replaceRoom={this.replaceRoom} />
                </Modal>
            </Auxiliary>
        );
    }
}

export default OperationRooms;
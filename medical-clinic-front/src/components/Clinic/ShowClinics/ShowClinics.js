import React, { Component } from 'react';
import axios from '../../../axios.js';
import ReactTable from 'react-table-6';
import 'react-table-6/react-table.css';

class ShowClinics extends Component {

  state = {
    clinics: []
  }

  componentDidMount() {
    const token = localStorage.getItem('token');
    axios.get('/clinics/get', {
      headers: { 'Authorization': 'Bearer ' + token }
    })
      .then(res => {
        console.log(res)
        this.setState({ clinics: res.data })
      })
      .catch(err => console.log(err));
  }

  render() {
    // let clinic = this.state.clinics.map(clinic => {
    // });
    const columns = [{
      id: 'name',
      Header: 'Name',
      accessor: d => d.name
    },
    {
      id: 'address',
      Header: 'Address',
      accessor: d => d.address
    }]

    return (
      <div style={{height: 'calc(100vh - 100px)', width: '100%'}}>
        {<ReactTable data={this.state.clinics}
          pageSize={(this.state.clinics.length > 10) ? 10 : this.state.clinics.length}
          columns={columns}
          filterable={true} />}
      </div>
    );
  }
}

export default ShowClinics;

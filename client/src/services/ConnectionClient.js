// Get connections from database
export function getConnections(setData, setLoading, setError, connections, selectedRef )
{
    console.log("Requesting connections.")
    fetch("http://localhost:8080/connections") 
    .then( response => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response.json();
    })
    .then((data) => {
      setData(data);
      setLoading(false);
      setError(null); // Clear any previous errors
      data.forEach(connection => {
        connections.push({id: connection.connectionId, selected: selectedRef.current});
      })
    })
    .catch((error) => {
      setError(error.message);
      setLoading(false);
    });
    console.log("Response received.")
}

// Save Connection to database
export async function saveConnection(event, formData)
{
  event.preventDefault();
  try {  
      const response = await fetch("http://localhost:8080/connections", {
          method: "POST",
          headers: {
              "Content-Type": "application/json",
          },
          body: JSON.stringify(formData) 
      });
      if (response.ok) {
          const result = await response.text();
          alert(result);
      } else {
          alert("Error: " + response.statusText);
      }
  } catch (error) {
      console.error("Error:" + error);
  }
}

// Delete Connections to database
export async function deleteConnections(event, idList) {
  event.preventDefault();

  try {
    const response = await fetch("http://localhost:8080/delete/connections", {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json"
        // Add Authorization if needed
      },
      body: JSON.stringify(idList)
    });

    if (response.ok) {
      console.log("Bulk delete successful");
    } else {
      console.error("Bulk delete failed:", response.status);
    }
  } catch (error) {
    console.error("Error during delete request:", error);
  }
}

// Get Five9 Connections from database
export function getFive9Connections(setData, setLoading, setError)
{
  fetch("http://localhost:8080/five9-connections") 
  .then((response) => {
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    return response.json();
  })
  .then((data) => {
    setData(data);
    setLoading(false);
    setError(null); // Clear any previous errors
  })
  .catch((error) => {
    setError(error.message);
    setLoading(false);
  });
}
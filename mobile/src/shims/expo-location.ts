export async function requestForegroundPermissionsAsync() {
  return { status: "granted", granted: true };
}

export async function getCurrentPositionAsync() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error("Konum desteklenmiyor"));
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (position) => resolve({ coords: position.coords }),
      reject
    );
  });
}

export default { requestForegroundPermissionsAsync, getCurrentPositionAsync };

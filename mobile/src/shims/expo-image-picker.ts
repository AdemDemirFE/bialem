export async function requestMediaLibraryPermissionsAsync() {
  return { granted: true, status: "granted" };
}

export async function launchImageLibraryAsync() {
  return { canceled: true, assets: [] };
}

export default {
  requestMediaLibraryPermissionsAsync,
  launchImageLibraryAsync,
  MediaTypeOptions: { Images: "Images" }
};

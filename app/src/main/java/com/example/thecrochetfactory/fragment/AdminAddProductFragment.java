package com.example.thecrochetfactory.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.thecrochetfactory.R;
import com.example.thecrochetfactory.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AdminAddProductFragment extends Fragment {

    // Declare views and Firestore components
    EditText productName, productPrice, productCategory, productStockQuantity;
    Button uploadImage, add;
    ImageView productImage;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri selectedImageUri;
    FirebaseFirestore firestore;
    ProgressBar progressBar;  // Add progress bar for image upload

    public AdminAddProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_add_product, container, false);

        // Initialize views
        productName = view.findViewById(R.id.name);
        productPrice = view.findViewById(R.id.price);
        productCategory = view.findViewById(R.id.type);  // Category
        productStockQuantity = view.findViewById(R.id.stock_quantity); // Stock quantity
        productImage = view.findViewById(R.id.imageupload);
        uploadImage = view.findViewById(R.id.btnchoose);
        add = view.findViewById(R.id.btnAdd);
        progressBar = view.findViewById(R.id.progressBar);  // Initialize progress bar

        // Initialize Firebase components
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firestore = FirebaseFirestore.getInstance();

        // Initialize image selector
        initImageSelector();

        // Set listener for saving the product
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all fields are filled
                if (validateFields()) {
                    // Upload the image to Firebase Storage
                    uploadImageToFirebaseStorage();
                }
            }
        });

        return view;
    }

    private void initImageSelector() {
        // Open image selector when the button is clicked
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);  // 1 is the request code
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            // Set selected image in ImageView
            selectedImageUri = data.getData();
            productImage.setImageURI(selectedImageUri);
        }
    }

    private boolean validateFields() {
        // Validate product fields
        if (TextUtils.isEmpty(productName.getText().toString().trim())) {
            productName.setError("Product name is required");
            return false;
        }

        if (TextUtils.isEmpty(productPrice.getText().toString().trim())) {
            productPrice.setError("Product price is required");
            return false;
        }

        if (!isValidPrice(productPrice.getText().toString().trim())) {
            productPrice.setError("Invalid price");
            return false;
        }

        if (TextUtils.isEmpty(productCategory.getText().toString().trim())) {
            productCategory.setError("Product category is required");
            return false;
        }

        if (TextUtils.isEmpty(productStockQuantity.getText().toString().trim())) {
            productStockQuantity.setError("Stock quantity is required");
            return false;
        }

        return true;
    }

    private boolean isValidPrice(String priceStr) {
        try {
            Double.parseDouble(priceStr); // Try to parse the price as a double
            return true;
        } catch (NumberFormatException e) {
            return false; // If parsing fails, the price is invalid
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (selectedImageUri != null) {
            progressBar.setVisibility(View.VISIBLE); // Show progress bar during upload

            // Upload image to Firebase Storage
            StorageReference imageRef = storageReference.child("images/" + UUID.randomUUID().toString());
            UploadTask uploadTask = imageRef.putFile(selectedImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressBar.setVisibility(View.INVISIBLE);  // Hide progress bar
                    Toast.makeText(getActivity(), "Error uploading image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get the image URL after successful upload
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("ImageUpload", "Image uploaded successfully");
                            String imageUrl = uri.toString();  // Store the URL
                            storeProductData(imageUrl);  // Call the method to store product data
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeProductData(String imageUrl) {
        // Get the input values
        String name = productName.getText().toString().trim();
        String category = productCategory.getText().toString().trim();
        double price = Double.parseDouble(productPrice.getText().toString().trim());
        int stockQuantity = Integer.parseInt(productStockQuantity.getText().toString().trim());

        // Create a new Product object with the entered data
        Product newProduct = new Product(
                UUID.randomUUID().toString(),  // Generate unique product ID
                name,
                category,
                imageUrl,
                price,
                stockQuantity
        );

        // Store the product data in Firestore
        firestore.collection("products").add(newProduct)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        newProduct.setProductId(documentReference.getId());  // Set the product ID after successful creation
                        Toast.makeText(getActivity(), "Product added successfully", Toast.LENGTH_SHORT).show();
                        clearFields();  // Clear input fields
                        progressBar.setVisibility(View.INVISIBLE);  // Hide progress bar after completion
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error storing product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);  // Hide progress bar after failure
                    }
                });
    }

    private void clearFields() {
        // Clear input fields and image
        productName.setText("");
        productPrice.setText("");
        productCategory.setText("");
        productStockQuantity.setText("");
        productImage.setImageResource(0);  // Clear the image
    }
}



//package com.example.thecrochetfactory.fragment;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import android.provider.MediaStore;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.example.thecrochetfactory.R;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import com.example.thecrochetfactory.model.Product;
//
//import java.util.UUID;
//
//public class AdminAddProductFragment extends Fragment {
//
//    // Declare views and Firestore components
//    EditText productName, productPrice, productCategory, productStockQuantity;
//    Button uploadImage, add;
//    ImageView productImage;
//    FirebaseStorage storage;
//    StorageReference storageReference;
//    Uri selectedImageUri;
//    FirebaseFirestore firestore;
//
//    public AdminAddProductFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_admin_add_product, container, false);
//
//        // Initialize views
//        productName = view.findViewById(R.id.name);
//        productPrice = view.findViewById(R.id.price);
//        productCategory = view.findViewById(R.id.type);  // Category
//        productStockQuantity = view.findViewById(R.id.stock_quantity); // Stock quantity
//        productImage = view.findViewById(R.id.imageupload);
//        uploadImage = view.findViewById(R.id.btnchoose);
//        add = view.findViewById(R.id.btnAdd);
//
//        // Initialize Firebase components
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
//        firestore = FirebaseFirestore.getInstance();
//
//        // Initialize image selector
//        initImageSelector();
//
//        // Set listener for saving the product
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Check if all fields are filled
//                if (validateFields()) {
//                    // Upload the image to Firebase Storage
//                    uploadImageToFirebaseStorage(productImage);
//                }
//            }
//        });
//
//        return view;
//    }
//
//    private void initImageSelector() {
//        // Open image selector when the button is clicked
//        uploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, 1);  // 1 is the request code
//            }
//        });
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
//            // Set selected image in ImageView
//            selectedImageUri = data.getData();
//            productImage.setImageURI(selectedImageUri);
//            productImage.setTag(selectedImageUri);  // Store the URI as a tag for later use
//        }
//    }
//
//    private boolean validateFields() {
//        // Validate product fields
//        if (TextUtils.isEmpty(productName.getText().toString().trim())) {
//            productName.setError("Product name is required");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(productPrice.getText().toString().trim())) {
//            productPrice.setError("Product price is required");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(productCategory.getText().toString().trim())) {
//            productCategory.setError("Product category is required");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(productStockQuantity.getText().toString().trim())) {
//            productStockQuantity.setError("Stock quantity is required");
//            return false;
//        }
//
//        return true;
//    }
//
//    private void uploadImageToFirebaseStorage(ImageView imageView) {
//        Uri selectedImage = (Uri) imageView.getTag();
//        if (selectedImage != null) {
//            // Upload image to Firebase Storage
//            StorageReference imageRef = storageReference.child("images/" + UUID.randomUUID().toString());
//            UploadTask uploadTask = imageRef.putFile(selectedImage);
//
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    Toast.makeText(getActivity(), "Error uploading image", Toast.LENGTH_SHORT).show();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // Get the image URL after successful upload
//                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Log.d("ImageUpload", "Image uploaded successfully");
//                            String imageUrl = uri.toString();  // Store the URL
//                            storeProductData(imageUrl);  // Call the method to store product data
//                        }
//                    });
//                }
//            });
//        } else {
//            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void storeProductData(String imageUrl) {
//        // Get the input values
//        String name = productName.getText().toString().trim();
//        String category = productCategory.getText().toString().trim();
//        double price = Double.parseDouble(productPrice.getText().toString().trim());
//        int stockQuantity = Integer.parseInt(productStockQuantity.getText().toString().trim());
//
//        // Create a new Product object with the entered data
//        Product newProduct = new Product(
//                UUID.randomUUID().toString(),  // Generate unique product ID
//                name,
//                category,
//                imageUrl,
//                price,
//                stockQuantity
//        );
//
//        // Store the product data in Firestore
//        firestore.collection("products").add(newProduct)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        newProduct.setProductId(documentReference.getId());  // Set the product ID after successful creation
//                        Toast.makeText(getActivity(), "Product added successfully", Toast.LENGTH_SHORT).show();
//                        clearFields();  // Clear input fields
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getActivity(), "Error storing product", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void clearFields() {
//        // Clear input fields and image
//        productName.setText("");
//        productPrice.setText("");
//        productCategory.setText("");
//        productStockQuantity.setText("");
//        productImage.setImageResource(0);  // Clear the image
//    }
//}



//package com.example.thecrochetfactory;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import android.provider.MediaStore;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class AdminAddProductFragment extends Fragment {
//
//    EditText productName, productPrice, productType;
//    Button uploadImage, add;
//    ImageView productImage;
//    FirebaseStorage storage;
//    StorageReference storageReference;
//    Uri selectedImageUri;
//    FirebaseFirestore firestore; // Firestore instance
//
//    public AdminAddProductFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_admin_add_product, container, false);
//
//        productName = view.findViewById(R.id.name);
//        productPrice = view.findViewById(R.id.price);
//        productType = view.findViewById(R.id.type);
//        productImage = view.findViewById(R.id.imageupload);
//
//        uploadImage = view.findViewById(R.id.btnchoose);
//        add = view.findViewById(R.id.btnAdd);
//
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
//        firestore = FirebaseFirestore.getInstance(); // Initialize Firestore
//
//        initImageSelector(); // select product image
//
//        // Set listener for saving the product
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Upload the image to Firebase Storage
//                uploadImageToFirebaseStorage(productImage);
//            }
//        });
//
//        return view;
//    }
//
//    private void initImageSelector() {
//        uploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Open image selector
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, 1);
//            }
//        });
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
//            selectedImageUri = data.getData();
//            productImage.setImageURI(selectedImageUri);
//            productImage.setTag(selectedImageUri);
//        }
//    }
//
//    private void uploadImageToFirebaseStorage(ImageView imageView) {
//        Uri selectedImage = ((Uri) imageView.getTag());
//        if (selectedImage != null) {
//            StorageReference imageRef = storageReference.child("images/" + UUID.randomUUID().toString());
//            UploadTask uploadTask = imageRef.putFile(selectedImage);
//
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    Toast.makeText(getActivity(), "Error uploading image", Toast.LENGTH_SHORT).show();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // Get the image URL
//                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Log.d("ImageUpload", "Image uploaded successfully");
//                            String imageUrl = uri.toString ();
//                            storeProductData(imageUrl); // Call the method to store product data
//                        }
//                    });
//                }
//            });
//        } else {
//            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void storeProductData(String imageUrl) {
//        String addproductName = productName.getText().toString().trim();
//        String addproductPrice = productPrice.getText().toString().trim();
//        String addproductType = productType.getText().toString().trim();
//
//        if (TextUtils.isEmpty(addproductName)) {
//            productName.setError("Product name is required");
//            return;
//        }
//        if (TextUtils.isEmpty(addproductPrice)) {
//            productPrice.setError("Product price is required");
//            return;
//        }
//        if (TextUtils.isEmpty(addproductType)) {
//            productType.setError("Product type is required");
//            return;
//        }
//
//        // Create a product object
//        Map<String, Object> product = new HashMap<>();
//        product.put("Productname", addproductName);
//        product.put("Producttype", addproductType);
//        product.put("Productprice", addproductPrice);
//        product.put("Image", imageUrl);
//        product.put("AddBy", "Admin");
//
//        // Store the product data in Firestore
//        firestore.collection("products").add(product)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Toast.makeText(getActivity(), "Product is successfully added", Toast.LENGTH_SHORT).show();
//                        clearFields();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getActivity(), "Error storing new product", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void clearFields() {
//        productName.setText("");
//        productPrice.setText("");
//        productType.setText("");
//        productImage.setImageResource(0); // Clear the image
//    }
//}